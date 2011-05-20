package org.openmrs.module.reporting.report.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.Timer;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Status;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.InteractiveReportRenderer;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.service.db.ReportDAO;
import org.openmrs.module.reporting.report.task.RunQueuedReportsTask;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.serializer.ReportingSerializer;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.util.HandlerUtil;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;

/**
 * Base Implementation of the ReportService API
 */
public class ReportServiceImpl extends BaseOpenmrsService implements ReportService {

	private static final String REPORT_RESULTS_DIR = "REPORT_RESULTS";
	
	public static final String RUN_QUEUED_REPORTS_TASK_NAME = "Run Queued Reports"; // Name of the task to run queued reports
	public static final String DELETE_OLD_REPORTS_TASK_NAME = "Delete Old Reports"; // Name of the task to delete old reports
	
	// Logger
	private transient Log log = LogFactory.getLog(this.getClass());

	// Private variables
	private ReportDAO reportDAO;
	private Map<ReportRequest, Report> reportCache = new LinkedHashMap<ReportRequest, Report>();
		
	/**
	 * Default constructor
	 */
	public ReportServiceImpl() { }
	
	//****** REPORT RENDERERS AND DESIGNS *****

	/** 
	 * @see ReportService#getReportDesignByUuid(String)
	 */
	public ReportDesign getReportDesignByUuid(String uuid) throws APIException {
		return reportDAO.getReportDesignByUuid(uuid);
	}
	
	/** 
	 * @see ReportService#getReportDesign(Integer)
	 */
	public ReportDesign getReportDesign(Integer id) throws APIException {
		return reportDAO.getReportDesign(id);
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
	 * @see ReportService#saveReportDesign(ReportDesign)
	 */
	public ReportDesign saveReportDesign(ReportDesign reportDesign) throws APIException {
		return reportDAO.saveReportDesign(reportDesign);
	}

	/** 
	 * @see ReportService#purgeReportDesign(ReportDesign)
	 */
	public void purgeReportDesign(ReportDesign reportDesign) {
		reportDAO.purgeReportDesign(reportDesign);
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
	 * @see ReportService#getPreferredReportRenderer()
	 */
	public ReportRenderer getPreferredReportRenderer(Class<Object> supportedType) {
		return HandlerUtil.getPreferredHandler(ReportRenderer.class, supportedType);
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
	
	//****** REPORT REQUESTS *****
	
	/**
	 * @see ReportService#saveReportRequest(ReportRequest)
	 */
	public ReportRequest saveReportRequest(ReportRequest request) {
		return reportDAO.saveReportRequest(request);
	}

	/**
	 * @see ReportService#getReportRequest(Integer)
	 */
	public ReportRequest getReportRequest(Integer id) {
		return reportDAO.getReportRequest(id);
	}

	/**
	 * @see ReportService#getReportRequestByUuid(String)
	 */
	public ReportRequest getReportRequestByUuid(String uuid) {
		return reportDAO.getReportRequestByUuid(uuid);
	}

	/**
	 * @see ReportService#getReportRequests(ReportDefinition, Date, Date, Status)
	 */
	public List<ReportRequest> getReportRequests(ReportDefinition reportDefinition, Date requestOnOrAfter, Date requestOnOrBefore, Status...statuses) {
		return reportDAO.getReportRequests(reportDefinition, requestOnOrAfter, requestOnOrBefore, statuses);
	}

	/**
	 * @see ReportService#purgeReportRequest(ReportRequest)
	 */
	public void purgeReportRequest(ReportRequest request) {
		reportDAO.purgeReportRequest(request);
		reportCache.remove(request);
		FileUtils.deleteQuietly(getReportDataFile(request));
		FileUtils.deleteQuietly(getReportErrorFile(request));
		FileUtils.deleteQuietly(getReportOutputFile(request));
	}
	
	//***** REPORTS *****
	
	/**
	 * @see ReportService#getReportDataFile(ReportRequest)
	 */
	public File getReportDataFile(ReportRequest request) {
		File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(REPORT_RESULTS_DIR);
		return new File(dir, request.getUuid() + ".reportdata.gz");
	}
	
	/**
	 * @see ReportService#getReportErrorFile(ReportRequest)
	 */
	public File getReportErrorFile(ReportRequest request) {
		File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(REPORT_RESULTS_DIR);
		return new File(dir, request.getUuid() + ".reporterror");
	}
	
	/**
	 * @see ReportService#getReportOutputFile(ReportRequest)
	 */
	public File getReportOutputFile(ReportRequest request) {
		File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(REPORT_RESULTS_DIR);
		return new File(dir, request.getUuid() + ".reportoutput");
	}
	
	/**
	 * @see ReportService#queueReport(ReportRequest)
	 */
	public ReportRequest queueReport(ReportRequest request) {
		ensureScheduledTasksRunning();
		return Context.getService(ReportService.class).saveReportRequest(request);
	}
	
	/**
	 * @see ReportService#saveReport(Report, String)
	 */
	public Report saveReport(Report report, String description) {
		persistReportToDisk(report);
		ReportRequest request = report.getRequest();
		request.setStatus(Status.SAVED);
		request.setDescription(description);
		Context.getService(ReportService.class).saveReportRequest(request);
		return report;
	}
	
	/**
	 * @see ReportService#runReport(ReportRequest)
	 */
	public Report runReport(ReportRequest request) {
		
		// Start up a timer to check performance
		Timer timer = Timer.start();
		
		// Set the status to processing and save the request
		request.setStatus(Status.PROCESSING);
		request.setEvaluateStartDatetime(new Date());
		log.info("Processing started for report request: " + request);
		
		// Construct a new report object to return
		Report report = new Report(request);

		try {
			// Create a new Evaluation Context, setting the base cohort from the request
			EvaluationContext context = new EvaluationContext();
			if (request.getBaseCohort() != null) {
				try {
					Cohort baseCohort = Context.getService(CohortDefinitionService.class).evaluate(request.getBaseCohort(), context);
					context.setBaseCohort(baseCohort);
					log.info(timer.logInterval("Evaluated the baseCohort to : " + baseCohort.size() + " patients"));
				} 
				catch (Exception ex) {
					throw new EvaluationException("baseCohort", ex);
				}
			}
		
			// Evaluate the Report Definition, any EvaluationException thrown by the next line can bubble up; wrapping it won't provide useful information
			ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
			ReportData reportData = rds.evaluate(request.getReportDefinition(), context);
			report.setReportData(reportData);
			request.setEvaluateCompleteDatetime(new Date());
			log.info(timer.logInterval("Evaluated the report into a ReportData successfully"));
			
			// Render the Report if appropriate
			if (request.getRenderingMode() != null) {
				ReportRenderer renderer = request.getRenderingMode().getRenderer();
				String argument = request.getRenderingMode().getArgument();
				if (!(renderer instanceof InteractiveReportRenderer)) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
		            renderer.render(reportData, argument, out);
		            report.setRenderedOutput(out.toByteArray());
		            request.setRenderCompleteDatetime(new Date());
		            log.info(timer.logInterval("Evaluated the report into a Rendered Output successfully"));
	            }
			}
			request.setStatus(Status.COMPLETED);
		}
		catch (Throwable t) {
			request.setStatus(Status.FAILED);
			try {
				StringWriter sw = new StringWriter();
				t.printStackTrace(new PrintWriter(sw));
				report.setErrorMessage(sw.toString());
	            log.info(timer.logInterval("Recorded a Report Error"));
			}
			catch (Exception e) {
				log.warn("Unable to log reporting error to file.", e);
			}
		}
			
		// Cache the report
		cacheReport(report);

		Context.getService(ReportService.class).saveReportRequest(request);
		log.info(timer.logInterval("Completed Running the Report"));
		return report;
	}
	 
	/**
	 * @see ReportService#getCachedReports()
	 */
	public Map<ReportRequest, Report> getCachedReports() {
		return new LinkedHashMap<ReportRequest, Report>(reportCache);
	}
	
	/**
	 * Loads the ReportData previously generated Report for the given ReportRequest, first checking the cache
	 */
	public ReportData loadReportData(ReportRequest request) {
		log.debug("Loading ReportData for ReportRequest");
		Report report = reportCache.get(request);
		if (report != null) {
			return report.getReportData();
		}
		try {
			long t1 = System.currentTimeMillis();
			String s = ReportUtil.readStringFromFile(getReportDataFile(request));
			ReportData reportData = Context.getSerializationService().deserialize(s, ReportData.class, ReportingSerializer.class);
			long t2 = System.currentTimeMillis();
			log.info("Loaded and Deserialized ReportData from file in " + (int)((t2-t1)/1000) + " seconds");
			return reportData;
		}
		catch (Exception e) {
			log.warn("Failed to load ReportData from disk for request " + request + " due to " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Loads the Rendered Output for a previously generated Report for the given ReportRequest, first checking the cache
	 */
	public byte[] loadRenderedOutput(ReportRequest request) {
		log.debug("Loading Rendered Output for ReportRequest");
		Report report = reportCache.get(request);
		if (report != null) {
			return report.getRenderedOutput();
		}
		try {
			return ReportUtil.readByteArrayFromFile(getReportOutputFile(request));
		}
		catch (Exception e) {
			log.warn("Failed to load Rendered Output from disk for request " + request + " due to " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Loads the Error message for a previously generated Report for the given ReportRequest
	 */
	public String loadReportError(ReportRequest request) {
		log.debug("Loading Report Error Output for ReportRequest");
		try {
			return ReportUtil.readStringFromFile(getReportErrorFile(request));
		}
		catch (Exception e) {
			log.warn("Failed to load Report Error from disk for request " + request + " due to " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Loads a previously generated Report for the given ReportRequest, first checking the cache
	 */
	public Report loadReport(ReportRequest request) {
		log.info("Loading Report for ReportRequest");
		Report report = reportCache.get(request);
		if (report == null) {
			report = new Report(request);
			report.setReportData(loadReportData(request));
			report.setRenderedOutput(loadRenderedOutput(request));
			cacheReport(report);
		}
		return report;
	}
	
	/**
	 * @see ReportService#ensureScheduledTasksRunning()
	 */
	public void ensureScheduledTasksRunning() {
		try {
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);
			TaskDefinition runTask = Context.getSchedulerService().getTaskByName(RUN_QUEUED_REPORTS_TASK_NAME);
			try {
				if (runTask == null) {
					runTask = new TaskDefinition();
					runTask.setUuid(UUID.randomUUID().toString());
					runTask.setTaskClass(RunQueuedReportsTask.class.getName());
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
		    	deleteTask.setUuid(UUID.randomUUID().toString());
				deleteTask.setTaskClass(DeleteOldReportsTask.class.getName());
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
	 * @see ReportService#deleteOldReportRequests()
	 */
	public void deleteOldReportRequests() {
		int ageInHoursToDelete = 72;
		try {
			ageInHoursToDelete = Integer.parseInt(Context.getAdministrationService().getGlobalProperty(ReportingConstants.GLOBAL_PROPERTY_DELETE_REPORTS_AGE_IN_HOURS));
		} 
		catch (Exception ex) {
			log.warn("Illegal value for " + ReportingConstants.GLOBAL_PROPERTY_DELETE_REPORTS_AGE_IN_HOURS + " global property. Using default value of 72.", ex);
		}
		if (ageInHoursToDelete <= 0) {
			return;
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -1*ageInHoursToDelete);
		for (ReportRequest request : getReportRequests(null, null, cal.getTime(), Status.COMPLETED,Status.FAILED)) {
			purgeReportRequest(request);
		}
    }
	
	//***** PRIVATE UTILITY METHODS *****
	
	/**
	 * @param report the Report to cache
	 */
	protected synchronized void cacheReport(Report report) {
		try {
			if (reportCache.size() >= ReportingConstants.GLOBAL_PROPERTY_MAX_CACHED_REPORTS()) {
				Iterator<ReportRequest> i = reportCache.keySet().iterator();
				i.next();
				i.remove();
			}
			reportCache.put(report.getRequest(), report);
		}
		catch (Exception e) {
			log.warn("Error caching Report", e);
		}
	}
	
	/**
	 * Saves the passed Report to disk within 3 separate files containing report data, output, and errors
	 */
	protected void persistReportToDisk(Report report) {
		
		Timer timer = Timer.start();
		
		// Serialize the raw data to file
		try {
			String serializedData = Context.getSerializationService().serialize(report.getReportData(), ReportingSerializer.class);
			log.info(timer.logInterval("Serialized the ReportData"));
			ReportUtil.writeStringToFile(getReportDataFile(report.getRequest()), serializedData);
			log.info(timer.logInterval("Persisted the report data to disk"));
		}
		catch (Exception e) {
			log.warn("An error occurred writing report data to disk", e);
		}
		
		// Write the output to file
		if (report.getRenderedOutput() != null) {
			try {
				ReportUtil.writeByteArrayToFile(getReportOutputFile(report.getRequest()), report.getRenderedOutput());
				log.info(timer.logInterval("Persisted the report output to disk"));
			}
			catch (Exception e) {
				log.warn("An error occurred writing report output to disk", e);
			}
		}
		
		// Write the error to file
		if (report.getErrorMessage() != null) {
			try {
				ReportUtil.writeStringToFile(getReportErrorFile(report.getRequest()), report.getErrorMessage());
				log.info(timer.logInterval("Persisted the report error to disk"));
			}
			catch (Exception e) {
				log.warn("An error occurred writing report error to disk", e);
			}
		}
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
}