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
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.renderer.RenderingMode;
import org.openmrs.module.report.renderer.ReportRenderer;
import org.openmrs.util.HandlerUtil;

/**
 * Base Implementation of the ReportService API
 */
public class BaseReportService extends BaseOpenmrsService implements ReportService {

	// Logger
	private transient Log log = LogFactory.getLog(this.getClass());

	// Data access object
	private SerializedObjectDAO serializedObjectDAO;

	/**
	 * @return serializedObjectDAO
	 */
	public SerializedObjectDAO getDao() {
		return serializedObjectDAO;
	}
	
	/**
	 * @param serializedObjectDAO
	 */
	public void setDao(SerializedObjectDAO serializedObjectDAO) {
		this.serializedObjectDAO = serializedObjectDAO;
	}	
	
	/**
	 * Default constructor
	 */
	public BaseReportService() { }

	/**
	 * @see ReportService#saveReportDefinition(ReportDefinition)
	 */
	public ReportDefinition saveReportDefinition(ReportDefinition reportDefinition) throws APIException {
		return serializedObjectDAO.saveObject(reportDefinition);
	}
	
	/**
	 * @see ReportService#getReportDefinition(Integer)
	 */
	public ReportDefinition getReportDefinition(Integer reportDefinitionId) throws APIException {
		return serializedObjectDAO.getObject(ReportDefinition.class, reportDefinitionId);
	}

	/**
	 * @see ReportService#getReportDefinitionByUuid(String)
	 */
	public ReportDefinition getReportDefinitionByUuid(String uuid) throws APIException {
		return serializedObjectDAO.getObjectByUuid(ReportDefinition.class, uuid);
	}
	
	/**
	 * @see ReportService#getReportDefinitions()
	 */
	public List<ReportDefinition> getReportDefinitions() throws APIException {
		return serializedObjectDAO.getAllObjects(ReportDefinition.class);
	}
	
	/**
	 * @see ReportService#deleteReportDefinition(ReportDefinition)
	 */
	public void deleteReportDefinition(ReportDefinition reportDefinition) {
		serializedObjectDAO.purgeObject(reportDefinition.getId());
	}

	/**
	 * @see ReportService#getReportRenderers()
	 */
	public Collection<ReportRenderer> getReportRenderers() {
		return HandlerUtil.getHandlersForType(ReportRenderer.class, null);
	}

	/**
	 * @see ReportService#evaluate(ReportDefinition, Cohort, EvaluationContext)
	 */
	@SuppressWarnings("unchecked")
	public ReportData evaluate(ReportDefinition reportDefinition, EvaluationContext evalContext) {
		
		log.debug("Evaluating report: " + reportDefinition + "(" + evalContext.getParameterValues() + ")");
		
		ReportData ret = new ReportData();
		Map<String, DataSet> data = new HashMap<String, DataSet>();
		ret.setDataSets(data);
		ret.setReportDefinition(reportDefinition);
		ret.setEvaluationContext(evalContext);
		
		Cohort baseCohort = CohortFilter.filter(evalContext, reportDefinition.getBaseCohortDefinition());
		
		DataSetDefinitionService dss = Context.getService(DataSetDefinitionService.class);
		if (reportDefinition.getDataSetDefinitions() != null) {
			for (Mapped<? extends DataSetDefinition> pd : reportDefinition.getDataSetDefinitions()) {
				EvaluationContext childEc = EvaluationContext.cloneForChild(evalContext, pd);
				childEc.setBaseCohort(baseCohort);
				data.put(pd.getParameterizable().getName(), dss.evaluate(pd.getParameterizable(), childEc));
			}
		}
		
		return ret;
	}
	
	/**
	 * @see ReportService#getRenderingModes(ReportDefinition)
	 */
	public List<RenderingMode> getRenderingModes(ReportDefinition schema) {
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

}