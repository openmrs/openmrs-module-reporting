package org.openmrs.module.reporting.report.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.definition.service.SerializedDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.InteractiveReportRenderer;
import org.openmrs.module.reporting.report.renderer.RenderingException;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.service.db.ReportDAO;
import org.openmrs.module.reporting.serializer.ReportingSerializer;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.serialization.OpenmrsSerializer;
import org.openmrs.serialization.SerializationException;
import org.openmrs.util.HandlerUtil;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

/**
 * Base Implementation of the ReportService API
 */
public class ReportServiceImpl extends BaseOpenmrsService implements ReportService {

	private final static String REPORT_RESULTS_DIR = "REPORT_RESULTS";

	private static final String REPORT_REQUEST_FILE_EXTENSION = ".request";
	private static final String REPORT_RESULTS_FILE_EXTENSION = ".report";
	
	// Logger
	private transient Log log = LogFactory.getLog(this.getClass());

	// Data access object
	private ReportDAO reportDAO;
	
	// queue of reports waiting to be run
	private Queue<ReportRequest> queue = new PriorityBlockingQueue<ReportRequest>();

	// reports that are currently being run
	private Set<ReportRequest> inProgress = new LinkedHashSet<ReportRequest>();
	
	// history of run reports
	private List<ReportRequest> reportRequestHistory;
	
	// Name of the task to run queued reports
	public static final String RUN_QUEUED_REPORTS_TASK_NAME = "Run Queued Reports";
	
	// Name of the task to delete old reports
	public static final String DELETE_OLD_REPORTS_TASK_NAME = "Delete Old Reports";
		
	/**
	 * Default constructor
	 */
	public ReportServiceImpl() { }
	
	/**
	 * Utility method that returns the SerializedDefinitionService
	 */
	public SerializedDefinitionService getService() {
		return Context.getService(SerializedDefinitionService.class);
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
			return (ReportRenderer) Context.loadClass(className).newInstance();
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
	 * @see ReportService#getRenderingModes(ReportDefinition)
	 */
	public List<RenderingMode> getRenderingModes(ReportDefinition reportDefinition) {
		List<RenderingMode> renderingModes = new Vector<RenderingMode>();
		if (reportDefinition != null) {
			for (ReportRenderer renderer : getReportRenderers()) {
				if (renderer.canRender(reportDefinition)) {
					Collection<RenderingMode> modes = renderer.getRenderingModes(reportDefinition);
					if (modes != null) {
						renderingModes.addAll(modes);
					}
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
	 * @see org.openmrs.module.reporting.report.service.ReportService#queueReport(org.openmrs.module.reporting.report.ReportRequest)
	 */
	public ReportRequest queueReport(ReportRequest request) {
		queue.add(request);

		// TODO: move this somewhere so it starts automatically
		ensureScheduledTasksRunning();
		
		return request;
    }
	
	/**
	 * This implementation runs this request directly, it does not queue it.
	 * @throws EvaluationException
	 * @see org.openmrs.module.reporting.report.service.ReportService#runReport(org.openmrs.module.reporting.report.ReportRequest)
	 */
	public Report runReport(ReportRequest request) throws EvaluationException {
		// Note: at some point we were saving the completed Report to disk. We stopped doing this
		// for the moment because deserializing it via xstream was painfully slow, but some of
		// the code here may be leftover from that.
		
		// TODO: move this somewhere so it starts automatically
		ensureScheduledTasksRunning();
		
		request.setUuid(UUID.randomUUID().toString());
		request.setRequestDate(new Date());
		request.setRequestedBy(Context.getAuthenticatedUser());
		Report ret = new Report(request);
		
		saveReportRequest(request);
		
		ret.startEvaluating();
		
		EvaluationContext ec = new EvaluationContext();

		if (request.getBaseCohort() != null) {
			try {
				Cohort baseCohort = Context.getService(CohortDefinitionService.class).evaluate(request.getBaseCohort(), ec);
				ec.setBaseCohort(baseCohort);
			} catch (Exception ex) {
				throw new EvaluationException("baseCohort", ex);
			}
		}
		
		ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
		// any EvaluationException thrown by the next line can bubble up. wrapping it won't provide useful information
		ReportData rawData = rds.evaluate(request.getReportDefinition(), ec);
		
		ret.rawDataEvaluated(rawData);
		
		File renderedFile = null;
		if (request.getRenderingMode() != null) {
			if (!(request.getRenderingMode().getRenderer() instanceof InteractiveReportRenderer)) {
				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					RenderingMode rm = request.getRenderingMode();
		            rm.getRenderer().render(rawData, rm.getArgument(), out);
		            
		            ret.outputRendered(
		            	rm.getRenderer().getFilename(request.getReportDefinition().getParameterizable(), rm.getArgument()),
		            	rm.getRenderer().getRenderedContentType(request.getReportDefinition().getParameterizable(), rm.getArgument()),
		            	out.toByteArray());
		            
		            // now save the rendered output to a file
		            File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(REPORT_RESULTS_DIR);
		    	    renderedFile = new File(dir, request.getUuid() + ".rendered." + ret.getRenderedFilename());
		            OpenmrsUtil.copyFile(new ByteArrayInputStream(ret.getRenderedOutput()), new BufferedOutputStream(new FileOutputStream(renderedFile)));
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
		
		if (renderedFile != null) {
			request.setRenderedOutput(renderedFile);
			saveReportRequest(request);
		}
		addToHistory(request);
		return ret;
    }
	
	/**
	 * @see org.openmrs.module.reporting.report.service.ReportService#getCompletedReportRequests()
	 */
	public List<ReportRequest> getCompletedReportRequests() {
	    return Collections.unmodifiableList(getReportRequestHistory());
    }

	/**
	 * @see org.openmrs.module.reporting.report.service.ReportService#getQueuedReportRequests()
	 */
	public List<ReportRequest> getQueuedReportRequests() {
		return Collections.unmodifiableList(new ArrayList<ReportRequest>(queue));
    }

	/**
	 * @see org.openmrs.module.reporting.report.service.ReportService#getReport(org.openmrs.module.reporting.report.ReportRequest)
	 */
	public Report getReport(ReportRequest request) {
		try {
			return loadReportFromFile(request.getUuid());
		} catch (Exception ex) {
			return null;
		}
    }

	/**
	 * @see org.openmrs.module.reporting.report.service.ReportService#getSavedReportRequests()
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
	 * @see org.openmrs.module.reporting.report.service.ReportService#archiveReportRequest(org.openmrs.module.reporting.report.ReportRequest)
	 */
	public void archiveReportRequest(ReportRequest request) {
	    request.setSaved(true);
	    saveReportRequest(request);
    }

	/**
	 * @see org.openmrs.module.reporting.report.service.ReportService#addToHistory(org.openmrs.module.reporting.report.ReportRequest)
	 */
	public void addToHistory(ReportRequest request) {
	    getReportRequestHistory().add(request);
    }

	/**
	 * @see org.openmrs.module.reporting.report.service.ReportService#deleteReportRequest(java.lang.String)
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
	 * @see org.openmrs.module.reporting.report.service.ReportService#getReportRequestByUuid(java.lang.String)
	 */
	public ReportRequest getReportRequestByUuid(String uuid) {
	    for (ReportRequest request : getReportRequestHistory()) {
	    	if (request.getUuid().equals(uuid))
	    		return request;
	    }
	    return null;
    }
	
	
	/**
	 * @see org.openmrs.module.reporting.report.service.ReportService#saveReportRequest(org.openmrs.module.reporting.report.ReportRequest)
	 */
	public void saveReportRequest(ReportRequest request) {
	    //Report report = loadReportFromFile(request.getUuid());
	    //report.setRequest(request);
	    //saveReportToFile(report);
		saveToFileHelper(request, request.getUuid() + ".request");
    }
	

	/**
	 * @see org.openmrs.module.reporting.report.service.ReportService#getReportByUuid(java.lang.String)
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
	 * @see org.openmrs.module.reporting.report.service.ReportService#getLastReportRequestsByReport()
	 */
	public Map<ReportDefinition, ReportRequest> getLastReportRequestsByReport() {
	    Map<ReportDefinition, ReportRequest> ret = new HashMap<ReportDefinition, ReportRequest>();
	    for (ReportRequest req : getReportRequestHistory())
	    	ret.put(req.getReportDefinition().getParameterizable(), req);
	    return ret;
    }

	/**
	 * @see org.openmrs.module.reporting.report.service.ReportService#deleteOldReportRequests()
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
		for (ReportRequest req : getReportRequestHistory()) {
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
	 * @see org.openmrs.module.reporting.report.service.ReportService#ensureScheduledTasksRunning()
	 */
	public void ensureScheduledTasksRunning() {
		try {
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);
			TaskDefinition runTask = Context.getSchedulerService().getTaskByName(RUN_QUEUED_REPORTS_TASK_NAME);
			try {
				if (runTask == null) {
					runTask = new TaskDefinition();
					runTask.setTaskClass("org.openmrs.module.reporting.report.service.RunQueuedReportsTask");
					runTask.setRepeatInterval(60l); // once per minute
					runTask.setStartOnStartup(true);
					runTask.setStartTime(null); // to induce immediate execution
					runTask.setName(RUN_QUEUED_REPORTS_TASK_NAME);
					runTask.setDescription("Runs queued reports. (If you stop this task, scheduled reports will not run.");
					Context.getSchedulerService().saveTask(runTask);
					Context.getSchedulerService().scheduleTask(runTask);
				} else {
					if (!runTask.getStarted()) {
						Context.getSchedulerService().scheduleTask(runTask);
					}
				}
			} catch (SchedulerException ex) {
				throw new APIException("Failed to schedule task to run queued reports", ex);
			}

		    TaskDefinition deleteTask = Context.getSchedulerService().getTaskByName(DELETE_OLD_REPORTS_TASK_NAME);
		    if (deleteTask == null) {
		    	deleteTask = new TaskDefinition();
				deleteTask.setTaskClass("org.openmrs.module.reporting.report.service.DeleteOldReportsTask");
				deleteTask.setRepeatInterval(60 * 60l); // hourly
				deleteTask.setStartOnStartup(true);
				deleteTask.setStartTime(null); // to induce immediate execution
				deleteTask.setName(DELETE_OLD_REPORTS_TASK_NAME);
				deleteTask.setDescription("Deletes reports that have not been saved and are older than the age specified in the global property.");
				try {
		            Context.getSchedulerService().scheduleTask(deleteTask);
	            }
	            catch (SchedulerException e) {
		            log.warn("Failed to schedule Delete Old Reports task. Old reports will not be automatically deleted", e);
	            }
		    }
		} finally {
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);
		}
    }
	
	/**
	 * @see org.openmrs.module.reporting.report.service.ReportService#maybeRunNextQueuedReport()
	 */
	public void maybeRunNextQueuedReport() {
	    if (queue.size() == 0)
	    	return;
	    
	    int maxAtATime = Integer.valueOf(Context.getAdministrationService().getGlobalProperty(ReportingConstants.GLOBAL_PROPERTY_MAX_REPORTS_TO_RUN, "1"));
	    if (inProgress.size() >= maxAtATime)
	    	return;
	    
	    ReportRequest next = queue.remove();
	    try {
	    	inProgress.add(next);
	    	ParameterizableUtil.refreshMappedDefinition(next.getReportDefinition());
	    	if (next.getBaseCohort() != null)
	    		ParameterizableUtil.refreshMappedDefinition(next.getBaseCohort());
	    	Report result = runReport(next);
	    }
        catch (Exception ex) {
	        log.error("Failed to run queued report", ex);
        } finally {
        	inProgress.remove(next);
	    }
	}
	
	/**
	 * @see org.openmrs.module.reporting.report.service.ReportService#getReportsCurrentlyRunning()
	 */
	public Collection<ReportRequest> getReportsCurrentlyRunning() {
	    return Collections.unmodifiableSet(inProgress);
    }
	
}