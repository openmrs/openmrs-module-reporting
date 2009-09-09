package org.openmrs.module.report.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import org.openmrs.module.report.Report;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.ReportDesign;
import org.openmrs.module.report.ReportRequest;
import org.openmrs.module.report.renderer.RenderingException;
import org.openmrs.module.report.renderer.RenderingMode;
import org.openmrs.module.report.renderer.ReportRenderer;
import org.openmrs.module.report.service.db.ReportDAO;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;
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
	private ReportDAO reportDAO;
	private SerializedObjectDAO serializedObjectDAO;
	private OpenmrsSerializer serializer;
	
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
	 * @see ReportService#getPreferredReportRenderer()
	 */
	public ReportRenderer getPreferredReportRenderer(Class<Object> supportedType) {
		return HandlerUtil.getPreferredHandler(ReportRenderer.class, supportedType);
	}
	
	/**
	 * @see ReportService#getPreferredReportRenderer()
	 */
	public ReportRenderer getReportRenderer(String className) {
		try { 
			return (ReportRenderer) Class.forName(className).newInstance();
		} catch(ClassNotFoundException e) { 
			/* ignore */
		} catch (IllegalAccessException e) { 
			/* ignore */
		} catch (InstantiationException e) {
			/* ignore */
		}
		
		return null;
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
	public List<RenderingMode> getRenderingModes(ReportDefinition reportDefinition) {
		List<RenderingMode> renderingModes = new Vector<RenderingMode>();
		if (reportDefinition != null) {
			for (ReportRenderer renderer : getReportRenderers()) {
				Collection<RenderingMode> modes = renderer.getRenderingModes(reportDefinition);
				if (modes != null) {
					renderingModes.addAll(modes);
				}
			}
			Collections.sort(renderingModes);
		}
		return renderingModes;
	}

	/** 
	 * @see ReportService#getAllReportDesigns(boolean)
	 */
	public List<ReportDesign> getAllReportDesigns(boolean includeRetired) {
		return reportDAO.getReportDesigns(null, null, includeRetired);
	}

	/** 
	 * @see ReportService#getAllReportDesigns(Integer, boolean)
	 */
	public List<ReportDesign> getReportDesigns(ReportDefinition reportDefinition, Class<? extends ReportRenderer> rendererType, 
											   boolean includeRetired) throws APIException {
		return reportDAO.getReportDesigns(reportDefinition, rendererType, includeRetired);
	}

	/** 
	 * @see ReportService#getReportDesign(Integer)
	 */
	public ReportDesign getReportDesign(Integer id) throws APIException {
		return reportDAO.getReportDesign(id);
	}

	/** 
	 * @see ReportService#getReportDesignByUuid(String)
	 */
	public ReportDesign getReportDesignByUuid(String uuid) throws APIException {
		return reportDAO.getReportDesignByUuid(uuid);
	}

	/** 
	 * @see ReportService#purgeReportDesign(ReportDesign)
	 */
	public void purgeReportDesign(ReportDesign reportDesign) {
		reportDAO.purgeReportDesign(reportDesign);
	}

	/** 
	 * @see ReportService#saveReportDesign(ReportDesign)
	 */
	public ReportDesign saveReportDesign(ReportDesign reportDesign) throws APIException {
		return reportDAO.saveReportDesign(reportDesign);
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the reportDAO
	 */
	public ReportDAO getReportDAO() {
		return reportDAO;
	}

	/**
	 * @param reportDAO the reportDAO to set
	 */
	public void setReportDAO(ReportDAO reportDAO) {
		this.reportDAO = reportDAO;
	}

	/**
	 * @return the serializedObjectDAO
	 */
	public SerializedObjectDAO getSerializedObjectDAO() {
		return serializedObjectDAO;
	}

	/**
	 * @param serializedObjectDAO the serializedObjectDAO to set
	 */
	public void setSerializedObjectDAO(SerializedObjectDAO serializedObjectDAO) {
		this.serializedObjectDAO = serializedObjectDAO;
	}

	/**
	 * @see org.openmrs.module.report.service.ReportService#queueReport(org.openmrs.module.report.ReportRequest)
	 */
	public ReportRequest queueReport(ReportRequest request) {
	    throw new APIException("Not Yet Implemented");
    }
	
	/**
	 * This implementation runs this request directly, it does not queue it.
	 * @see org.openmrs.module.report.service.ReportService#runReport(org.openmrs.module.report.ReportRequest)
	 */
	public Report runReport(ReportRequest request) {
		request.setUuid(UUID.randomUUID().toString());
		request.setRequestDate(new Date());
		Report ret = new Report(request);
		
		ret.startEvaluating();
		
		EvaluationContext ec = new EvaluationContext();
		ec.setParameterValues(request.getParameterValues());
		ReportData rawData = evaluate(request.getReportDefinition(), ec);
		
		ret.rawDataEvaluated(rawData);
		
		if (request.getReportRenderer() != null) {
			if (!(request.getReportRenderer() instanceof WebReportRenderer)) {
				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
		            request.getReportRenderer().render(rawData, request.getRenderArgument(), out);
		            
		            ret.outputRendered(out.toByteArray());
	            }
	            catch (RenderingException e) {
		            log.error("Failed to Render ReportData", e);
		            throw new APIException(e);
	            }
	            catch (IOException e) {
		            log.error("Failed to write rendered data to stream", e);
		            throw new APIException(e);
	            }
			}
		}
		return ret;
    }
}