package org.openmrs.module.report.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.GlobalProperty;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.cohort.definition.util.CohortFilter;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportSchema;
import org.openmrs.module.report.renderer.RenderingMode;
import org.openmrs.module.report.renderer.ReportRenderer;
import org.openmrs.util.HandlerUtil;

/**
 * Base Implementation of the ReportService API
 */
public class BaseReportService extends BaseOpenmrsService implements ReportService {

	private transient Log log = LogFactory.getLog(this.getClass());
	private SerializedObjectDAO serializedObjectDAO;
		
	/**
	 * Default constructor
	 */
	public BaseReportService() { }

	/**
	 * @see ReportService#saveReportSchema(ReportSchema)
	 */
	public ReportSchema saveReportSchema(ReportSchema reportSchema) throws APIException {
		return serializedObjectDAO.saveObject(reportSchema);
	}
	
	/**
	 * @see ReportService#getReportSchema(Integer)
	 */
	public ReportSchema getReportSchema(Integer reportSchemaId) throws APIException {
		return serializedObjectDAO.getObject(ReportSchema.class, reportSchemaId);
	}

	/**
	 * @see ReportService#getReportSchemaByUuid(String)
	 */
	public ReportSchema getReportSchemaByUuid(String uuid) throws APIException {
		return serializedObjectDAO.getObjectByUuid(ReportSchema.class, uuid);
	}
	
	/**
	 * @see ReportService#getReportSchemas()
	 */
	public List<ReportSchema> getReportSchemas() throws APIException {
		return serializedObjectDAO.getAllObjects(ReportSchema.class);
	}
	
	/**
	 * @see ReportService#deleteReportSchema(ReportSchema)
	 */
	public void deleteReportSchema(ReportSchema reportSchema) {
		serializedObjectDAO.purgeObject(reportSchema.getId());
	}

	/**
	 * @see ReportService#getReportRenderers()
	 */
	public Collection<ReportRenderer> getReportRenderers() {
		return HandlerUtil.getHandlersForType(ReportRenderer.class, null);
	}

	/**
	 * @see ReportService#evaluate(ReportSchema, Cohort, EvaluationContext)
	 */
	@SuppressWarnings("unchecked")
	public ReportData evaluate(ReportSchema reportSchema, EvaluationContext evalContext) {
		
		log.debug("Evaluating report: " + reportSchema + "(" + evalContext.getParameterValues() + ")");
		
		ReportData ret = new ReportData();
		Map<String, DataSet> data = new HashMap<String, DataSet>();
		ret.setDataSets(data);
		ret.setReportSchema(reportSchema);
		ret.setEvaluationContext(evalContext);
		
		Cohort baseCohort = CohortFilter.filter(evalContext, reportSchema.getBaseCohortDefinition());
		
		DataSetDefinitionService dss = Context.getService(DataSetDefinitionService.class);
		if (reportSchema.getDataSetDefinitions() != null) {
			for (Mapped<? extends DataSetDefinition> pd : reportSchema.getDataSetDefinitions()) {
				EvaluationContext childEc = EvaluationContext.cloneForChild(evalContext, pd);
				childEc.setBaseCohort(baseCohort);
				data.put(pd.getParameterizable().getName(), dss.evaluate(pd.getParameterizable(), childEc));
			}
		}
		
		return ret;
	}
	
	/**
	 * @see ReportService#getRenderingModes(ReportSchema)
	 */
	public List<RenderingMode> getRenderingModes(ReportSchema schema) {
		List<RenderingMode> ret = new Vector<RenderingMode>();
		for (ReportRenderer r : getReportRenderers()) {
			Collection<RenderingMode> modes = r.getRenderingModes(schema);
			if (modes != null) {
				ret.addAll(modes);
			}
		}
		Collections.sort(ret);
		return ret;
	}
	
	/**
	 * @see ReportService#getReportXmlMacros()
	 */
	public Properties getReportXmlMacros() {
		try {
			String macrosAsString = Context.getAdministrationService().getGlobalProperty("report.macros"); // TODO
			Properties macros = new Properties();
			if (macrosAsString != null) {
				macros.load(new ByteArrayInputStream(macrosAsString.getBytes("UTF-8")));
			}
			return macros;
		}
		catch (Exception ex) {
			throw new APIException(ex);
		}
	}
	
	/**
	 * @see ReportService#saveReportXmlMacros(Properties)
	 */
	public void saveReportXmlMacros(Properties macros) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			macros.store(out, null);
			GlobalProperty prop = new GlobalProperty("report.macros", out.toString()); // TODO
			Context.getAdministrationService().saveGlobalProperty(prop);
		}
		catch (Exception ex) {
			throw new APIException(ex);
		}
	}
	
	/**
	 * @see ReportService#applyReportXmlMacros(String)
	 */
	public String applyReportXmlMacros(String input) {
		Properties macros = getReportXmlMacros();
		if (macros != null && macros.size() > 0) {
			log.debug("XML Before macros: " + input);
			String prefix = macros.getProperty("macroPrefix", "");
			String suffix = macros.getProperty("macroSuffix", "");
			while (true) {
				String replacement = input;
				for (Map.Entry<Object, Object> e : macros.entrySet()) {
					String key = prefix + e.getKey() + suffix;
					String value = e.getValue() == null ? "" : e.getValue().toString();
					log.debug("Trying to replace " + key + " with " + value);
					replacement = replacement.replace(key, (String) e.getValue());
				}
				if (input.equals(replacement)) {
					log.debug("Macro expansion complete.");
					break;
				}
				input = replacement;
				log.debug("XML Exploded to: " + input);
			}
		}
		return input;
	}

	//************* Property Access *************

	/**
	 * @return serializedObjectDAO
	 */
	public SerializedObjectDAO getSerializedObjectDAO() {
		return serializedObjectDAO;
	}
	
	/**
	 * @param serializedObjectDAO
	 */
	public void setSerializedObjectDAO(SerializedObjectDAO serializedObjectDAO) {
		this.serializedObjectDAO = serializedObjectDAO;
	}
}