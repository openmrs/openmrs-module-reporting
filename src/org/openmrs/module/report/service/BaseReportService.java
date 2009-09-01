package org.openmrs.module.report.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
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
import org.openmrs.serialization.OpenmrsSerializer;
import org.openmrs.util.HandlerUtil;
import org.springframework.util.StringUtils;

/**
 * Base Implementation of the ReportService API
 */
public class BaseReportService extends BaseOpenmrsService implements ReportService {

	// Logger
	private transient Log log = LogFactory.getLog(this.getClass());

	// Data access object
	private SerializedObjectDAO serializedObjectDAO;
	private OpenmrsSerializer serializer;

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
     * @param serializer the serializer to set
     */
    public void setSerializer(OpenmrsSerializer serializer) {
    	this.serializer = serializer;
    }

	/**
	 * Default constructor
	 */
	public BaseReportService() { }

	/**
	 * @see ReportService#saveReportDefinition(ReportDefinition)
	 */
	public ReportDefinition saveReportDefinition(ReportDefinition reportDefinition) throws APIException {
		return serializedObjectDAO.saveObject(reportDefinition, serializer);
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
	 * @see ReportService#getReportDefinition(String, Class)
	 */
    public ReportDefinition getReportDefinition(String uuid, Class<? extends ReportDefinition> type) {
    	ReportDefinition r = null;
    	if (StringUtils.hasText(uuid)) {
        	r = Context.getService(ReportService.class).getReportDefinitionByUuid(uuid);
    	}
    	else if (type != null) {
     		try {
    			r = type.newInstance();
    		}
    		catch (Exception e) {
    			throw new IllegalArgumentException("Unable to instantiate a ReportDefinition of type: " + type);
    		}
    	}
    	else {
    		throw new IllegalArgumentException("You must supply either a uuid or a type");
    	}
    	return r;
    }
	
	/**
	 * @see ReportService#getReportDefinitions()
	 */
	public List<ReportDefinition> getReportDefinitions() throws APIException {
		return getReportDefinitions(false);
	}
	
	/**
	 * @see ReportService#getReportDefinitions(boolean)
	 */
	public List<ReportDefinition> getReportDefinitions(boolean includeRetired) throws APIException {
		return serializedObjectDAO.getAllObjects(ReportDefinition.class, includeRetired);
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
		ret.setDefinition(reportDefinition);
		ret.setContext(evalContext);
		
		Cohort baseCohort = CohortFilter.filter(evalContext, reportDefinition.getBaseCohortDefinition());
		
		DataSetDefinitionService dss = Context.getService(DataSetDefinitionService.class);
		if (reportDefinition.getDataSetDefinitions() != null) {
			for (String key : reportDefinition.getDataSetDefinitions().keySet()) {
				Mapped<? extends DataSetDefinition> pd = reportDefinition.getDataSetDefinitions().get(key);
				EvaluationContext childEc = EvaluationContext.cloneForChild(evalContext, pd);
				childEc.setBaseCohort(baseCohort);
				data.put(key, dss.evaluate(pd.getParameterizable(), childEc));
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


}