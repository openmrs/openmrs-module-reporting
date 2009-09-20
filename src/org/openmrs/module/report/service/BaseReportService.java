package org.openmrs.module.report.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
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
import org.openmrs.module.report.renderer.InteractiveReportRenderer;
import org.openmrs.module.report.renderer.RenderingException;
import org.openmrs.module.report.renderer.RenderingMode;
import org.openmrs.module.report.renderer.ReportRenderer;
import org.openmrs.module.report.service.db.ReportDAO;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.serializer.ReportingSerializer;
import org.openmrs.module.util.DateUtil;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.serialization.OpenmrsSerializer;
import org.openmrs.serialization.SerializationException;
import org.openmrs.util.HandlerUtil;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.StringUtils;

/**
 * Base Implementation of the ReportService API
 */
public class BaseReportService extends BaseOpenmrsService implements ReportService {

	private final static String REPORT_RESULTS_DIR = "REPORT_RESULTS";

	private static final String REPORT_REQUEST_FILE_EXTENSION = ".request";
	private static final String REPORT_RESULTS_FILE_EXTENSION = ".report";
	
	// Logger
	private transient Log log = LogFactory.getLog(this.getClass());

	// Data access object
	private ReportDAO reportDAO;
	private SerializedObjectDAO serializedObjectDAO;
	private OpenmrsSerializer serializer;
	
	// history of run reports
	private List<ReportRequest> reportRequestHistory;
	
	// Name of the task to delete old reportsconcept word update task.
	public static final String DELETE_OLD_REPORTS_TASK_NAME = "Delete Old Reports";
		
    /**
     * @param serializer the serializer to set
     */
    public void setSerializer(OpenmrsSerializer serializer) {
    	this.serializer = serializer;
    }

	/**
	 * Default constructor
	 */
	public BaseReportService() {
	}

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
		// TODO: move this somewhere so it starts automatically
		ensureDeleteOldReportsTask();
		
		request.setUuid(UUID.randomUUID().toString());
		request.setRequestDate(new Date());
		request.setRequestedBy(Context.getAuthenticatedUser());
		Report ret = new Report(request);
		
		ret.startEvaluating();
		
		EvaluationContext ec = new EvaluationContext();
		ec.setParameterValues(request.getParameterValues());

		if (request.getBaseCohort() != null) {
			Cohort baseCohort = Context.getService(CohortDefinitionService.class).evaluate(request.getBaseCohort(), ec);
			ec.setBaseCohort(baseCohort);
		}
		
		ReportData rawData = evaluate(request.getReportDefinition(), ec);
		
		ret.rawDataEvaluated(rawData);
		
		if (request.getRenderingMode() != null) {
			if (!(request.getRenderingMode().getRenderer() instanceof InteractiveReportRenderer)) {
				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					RenderingMode rm = request.getRenderingMode();
		            rm.getRenderer().render(rawData, rm.getArgument(), out);
		            
		            ret.outputRendered(
		            	rm.getRenderer().getFilename(request.getReportDefinition(), rm.getArgument()),
		            	rm.getRenderer().getRenderedContentType(request.getReportDefinition(), rm.getArgument()),
		            	out.toByteArray());
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

		saveReportToFile(ret);
		addToHistory(request);
		return ret;
    }
	
	/**
	 * @see org.openmrs.module.report.service.ReportService#getCompletedReportRequests()
	 */
	public List<ReportRequest> getCompletedReportRequests() {
	    return Collections.unmodifiableList(getReportRequestHistory());
    }

	/**
	 * @see org.openmrs.module.report.service.ReportService#getQueuedReportRequests()
	 */
	public List<ReportRequest> getQueuedReportRequests() {
		// TODO implement this
	    return Collections.emptyList();
    }

	/**
	 * @see org.openmrs.module.report.service.ReportService#getReport(org.openmrs.module.report.ReportRequest)
	 */
	public Report getReport(ReportRequest request) {
		try {
			return loadReportFromFile(request.getUuid());
		} catch (Exception ex) {
			return null;
		}
    }

	/**
	 * @see org.openmrs.module.report.service.ReportService#getSavedReportRequests()
	 */
	public List<ReportRequest> getSavedReportRequests() {
	    List<ReportRequest> ret = new ArrayList<ReportRequest>();
	    for (ReportRequest req : getReportRequestHistory()) {
	    	if (req.isSaved())
	    		ret.add(req);
	    }
	    return ret;
    }

	/**
	 * @see org.openmrs.module.report.service.ReportService#archiveReportRequest(org.openmrs.module.report.ReportRequest)
	 */
	public void archiveReportRequest(ReportRequest request) {
	    request.setSaved(true);
	    saveReportRequest(request);
    }

	/**
	 * @see org.openmrs.module.report.service.ReportService#addToHistory(org.openmrs.module.report.ReportRequest)
	 */
	public void addToHistory(ReportRequest request) {
	    getReportRequestHistory().add(request);
    }

	/**
	 * @see org.openmrs.module.report.service.ReportService#deleteReportRequest(java.lang.String)
	 */
	public void deleteFromHistory(String uuid) {
	    for (Iterator<ReportRequest> i = getReportRequestHistory().iterator(); i.hasNext(); ) {
	    	ReportRequest rr = i.next();
	    	if (uuid.equals(rr.getUuid())) {
	    		i.remove();
	    		deleteSavedReportFile(rr);
	    	}
	    }
    }
	
	
	/**
	 * @see org.openmrs.module.report.service.ReportService#getReportRequestByUuid(java.lang.String)
	 */
	public ReportRequest getReportRequestByUuid(String uuid) {
	    for (ReportRequest request : getReportRequestHistory()) {
	    	if (request.getUuid().equals(uuid))
	    		return request;
	    }
	    return null;
    }
	
	
	/**
	 * @see org.openmrs.module.report.service.ReportService#saveReportRequest(org.openmrs.module.report.ReportRequest)
	 */
	public void saveReportRequest(ReportRequest request) {
	    //Report report = loadReportFromFile(request.getUuid());
	    //report.setRequest(request);
	    //saveReportToFile(report);
		saveToFileHelper(request, request.getUuid() + ".request");
    }
	

	/**
	 * @see org.openmrs.module.report.service.ReportService#getReportByUuid(java.lang.String)
	 */
	public Report getReportByUuid(String uuid) {
	    return loadReportFromFile(uuid);
    }
	
	
	private List<ReportRequest> getReportRequestHistory() {
	    if (reportRequestHistory == null) {
	    	rebuildReportRequestHistory();
	    }
	    return reportRequestHistory;
    }
	
	private TaskExecutor executor;

	private void saveReportToFile(final Report report) {
		if (executor == null)
			executor = new SimpleAsyncTaskExecutor();
		saveToFileHelper(report.getRequest(), report.getRequest().getUuid() + ".request");
		executor.execute(new Runnable() {
			public void run() {
				Context.openSession();
				try {
					saveToFileHelper(report, report.getRequest().getUuid() + ".report");
				} finally {
					Context.closeSession();
				}
            }
		});
	}
	
	private ReportRequest loadRequestFromFile(String requestUuid) {
		log.info("Loading saved report request: " + requestUuid);
		OpenmrsSerializer serializer = Context.getSerializationService().getSerializer(ReportingSerializer.class);
		File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(REPORT_RESULTS_DIR);
		File file = new File(dir, requestUuid + REPORT_REQUEST_FILE_EXTENSION);
		if (!file.exists()) {
			throw new APIException("The persisted Report Request file is missing: " + file.getAbsolutePath());
		}
		BufferedReader r = null;
		try {
			r = new BufferedReader(new FileReader(file));
			StringBuilder sb = new StringBuilder();
			for (String s = r.readLine(); s != null; s = r.readLine()) {
				sb.append(s);
			}
			return serializer.deserialize(sb.toString(), ReportRequest.class);
		}
        catch (Exception ex) {
	        throw new APIException("Failed to load file: " + file.getAbsolutePath(), ex);
        } finally {
			try {
				r.close();
            } catch (IOException e) { }
		}
	}
	
	private Report loadReportFromFile(String requestUuid) {
		log.info("Loading saved report: " + requestUuid);
		OpenmrsSerializer serializer = Context.getSerializationService().getSerializer(ReportingSerializer.class);
		File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(REPORT_RESULTS_DIR);
		File file = new File(dir, requestUuid + REPORT_RESULTS_FILE_EXTENSION);
		if (!file.exists()) {
			throw new APIException("The persisted Report file is missing: " + file.getAbsolutePath());
		}
		BufferedReader r = null;
		try {
			r = new BufferedReader(new FileReader(file));
			StringBuilder sb = new StringBuilder();
			for (String s = r.readLine(); s != null; s = r.readLine()) {
				sb.append(s);
			}
			return serializer.deserialize(sb.toString(), Report.class);
		}
        catch (Exception ex) {
	        throw new APIException("Failed to load file: " + file.getAbsolutePath(), ex);
        } finally {
			try {
				r.close();
            } catch (IOException e) { }
		}
	}
	
	private void deleteSavedReportFile(ReportRequest request) {
		File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(REPORT_RESULTS_DIR);
		for (File file : dir.listFiles()) {
			if (file.getName().startsWith(request.getUuid()))
				file.delete();
		}
    }
	
	private void rebuildReportRequestHistory() {
		log.info("Rebuilding Report Request History...");
		Vector<ReportRequest> history = new Vector<ReportRequest>();
		File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(REPORT_RESULTS_DIR);
		for (File file : dir.listFiles()) {
			if (!file.getName().endsWith(REPORT_RESULTS_FILE_EXTENSION)) {
				continue;
			}
			String uuid = file.getName().substring(0, file.getName().indexOf("."));
			ReportRequest request = loadRequestFromFile(uuid);
			history.add(request);
		}
		Collections.sort(history, new Comparator<ReportRequest>() {
			public int compare(ReportRequest left, ReportRequest right) {
	            return left.getRequestDate().compareTo(right.getRequestDate());
            }
		});
		log.info("...Done Rebuilding Report Request History");
		reportRequestHistory = history;
    }

	private void saveToFileHelper(Object object, String filename) {
		log.info("Saving to: " + filename);
		File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(REPORT_RESULTS_DIR);
	    File file = new File(dir, filename);
	    if (file.exists()) {
	    	file.delete();
	    }
	    try {
	    	OpenmrsSerializer serializer = Context.getSerializationService().getSerializer(ReportingSerializer.class);
	    	PrintWriter wr = new PrintWriter(new FileWriter(file));
	    	wr.write(serializer.serialize(object));
	    	wr.close();
	    } catch (IOException ex) {
	    	throw new APIException("Error writing data file", ex);
	    } catch (SerializationException e) {
	        throw new APIException("Error serializing");
        }
    }

	/**
	 * @see org.openmrs.module.report.service.ReportService#getLastReportRequestsByReport()
	 */
	public Map<ReportDefinition, ReportRequest> getLastReportRequestsByReport() {
	    Map<ReportDefinition, ReportRequest> ret = new HashMap<ReportDefinition, ReportRequest>();
	    for (ReportRequest req : getReportRequestHistory())
	    	ret.put(req.getReportDefinition(), req);
	    return ret;
    }

	/**
	 * @see org.openmrs.module.report.service.ReportService#deleteOldReportRequests()
	 */
	public void deleteOldReportRequests() {
		int ageInHoursToDelete = 72;
		try {
			ageInHoursToDelete = Integer.parseInt(Context.getAdministrationService().getGlobalProperty(ReportingConstants.GLOBAL_PROPERTY_DELETE_REPORTS_AGE_IN_HOURS));
		} catch (Exception ex) {
			log.warn("Illegal value for " + ReportingConstants.GLOBAL_PROPERTY_DELETE_REPORTS_AGE_IN_HOURS + " global property. Using default value of 72.", ex);
		}
		if (ageInHoursToDelete <= 0)
			return;
		
		List<String> uuidsToDelete = new ArrayList<String>();
		Date now = new Date();
		for (ReportRequest req : reportRequestHistory) {
			if (!req.isSaved() && DateUtil.getHoursBetween(req.getRequestDate(), now) >= ageInHoursToDelete) {
				uuidsToDelete.add(req.getUuid());
			}
		}
		for (String uuid : uuidsToDelete) {
			try {
				deleteFromHistory(uuid);
			} catch (Exception ex) {
				log.warn("Error deleting old request " + uuid, ex);
			}
		}		
    }
	
	/**
	 * Makes sure there's a scheduled task registered to DeleteOldReports
	 */
	private void ensureDeleteOldReportsTask() {
	    TaskDefinition task = Context.getSchedulerService().getTaskByName(DELETE_OLD_REPORTS_TASK_NAME);
	    if (task == null) {
	    	task = new TaskDefinition();
			task.setTaskClass("org.openmrs.module.report.service.DeleteOldReportsTask");
			task.setRepeatInterval(60 * 60l); // hourly
			task.setStartOnStartup(true);
			task.setStartTime(null); // to induce immediate execution
			task.setName(DELETE_OLD_REPORTS_TASK_NAME);
			task.setDescription("Deletes reports that have not been saved and are older than the age specified in the global property.");
			try {
	            Context.getSchedulerService().scheduleTask(task);
            }
            catch (SchedulerException e) {
	            log.warn("Failed to schedule Delete Old Reports task. Old reports will not be automatically deleted", e);
            }
	    }
    }

}