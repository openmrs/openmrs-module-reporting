package org.openmrs.module.reporting.report.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.Timer;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportProcessorConfiguration;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Priority;
import org.openmrs.module.reporting.report.ReportRequest.PriorityComparator;
import org.openmrs.module.reporting.report.ReportRequest.Status;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.processor.ReportProcessor;
import org.openmrs.module.reporting.report.renderer.InteractiveReportRenderer;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.service.db.ReportDAO;
import org.openmrs.module.reporting.report.task.ReportingTimerTask;
import org.openmrs.module.reporting.report.task.RunQueuedReportsTask;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.serializer.ReportingSerializer;
import org.openmrs.util.HandlerUtil;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Base Implementation of the ReportService API
 */
public class ReportServiceImpl extends BaseOpenmrsService implements ReportService {

	public static final String GENERATED_BY = "generatedBy";
	public static final String GENERATION_DATE = "generationDate";
	
	// Logger
	private transient Log log = LogFactory.getLog(this.getClass());

	// Private variables
	private ReportDAO reportDAO;
	private ReportingTimerTask runQueuedReportsTask;
	private Map<String, CachedReportData> reportCache = Collections.synchronizedMap(new LinkedHashMap<String, CachedReportData>());
		
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
	 * @see ReportService#getReportDesigns(ReportDefinition, Class, boolean)
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
	 * @see ReportService#getReportRenderer(String)
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
	 * @see ReportService#getPreferredReportRenderer(Class)
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
	@Transactional
	public ReportRequest saveReportRequest(ReportRequest request) {
		return reportDAO.saveReportRequest(request);
	}

	/**
	 * @see ReportService#getReportRequest(Integer)
	 */
	@Transactional(readOnly=true)
	public ReportRequest getReportRequest(Integer id) {
		return reportDAO.getReportRequest(id);
	}

	/**
	 * @see ReportService#getReportRequestByUuid(String)
	 */
	@Transactional(readOnly=true)
	public ReportRequest getReportRequestByUuid(String uuid) {
		return reportDAO.getReportRequestByUuid(uuid);
	}

	/**
	 * @see ReportService#getReportRequests(ReportDefinition, Date, Date, Status[])
	 */
	@Transactional(readOnly=true)
	public List<ReportRequest> getReportRequests(ReportDefinition reportDefinition, Date requestOnOrAfter, Date requestOnOrBefore, Status...statuses) {
		return getReportRequests(reportDefinition, requestOnOrAfter, requestOnOrBefore, null, statuses);
	}

	/**
	 * @see ReportService#getReportRequests(ReportDefinition, Date, Date, Integer, Status[])
	 */
	@Transactional(readOnly=true)
	public List<ReportRequest> getReportRequests(ReportDefinition reportDefinition, Date requestOnOrAfter, Date requestOnOrBefore, Integer mostRecentNum, Status...statuses) {
		return reportDAO.getReportRequests(reportDefinition, requestOnOrAfter, requestOnOrBefore, mostRecentNum, statuses);
	}

	/**
	 * @see ReportService#purgeReportRequest(ReportRequest)
	 */
	@Transactional
	public void purgeReportRequest(ReportRequest request) {
		RunQueuedReportsTask reportsTask = RunQueuedReportsTask.getCurrentlyRunningRequests().get(request.getUuid());
		if (reportsTask != null) {
			reportsTask.cancelTask();
		}
		reportDAO.purgeReportRequest(request);
		reportCache.remove(request.getUuid());
		FileUtils.deleteQuietly(getReportDataFile(request));
		FileUtils.deleteQuietly(getReportErrorFile(request));
		FileUtils.deleteQuietly(getReportOutputFile(request));
		FileUtils.deleteQuietly(getReportLogFile(request));
	}
	
	//****** REPORT PROCESSOR CONFIGURATIONS *****
	
	/**
	 * @see ReportService#saveReportProcessorConfiguration(ReportProcessorConfiguration)
	 */
	public ReportProcessorConfiguration saveReportProcessorConfiguration(ReportProcessorConfiguration processorConfiguration) {
		return reportDAO.saveReportProcessorConfiguration(processorConfiguration);
	}

	/**
	 * @see ReportService#getReportProcessorConfiguration(Integer)
	 */
	public ReportProcessorConfiguration getReportProcessorConfiguration(Integer id) {
		return reportDAO.getReportProcessorConfiguration(id);
	}

	/**
	 * @see ReportService#getReportProcessorConfigurationByUuid(String)
	 */
	public ReportProcessorConfiguration getReportProcessorConfigurationByUuid(String uuid) {
		return reportDAO.getReportProcessorConfigurationByUuid(uuid);
	}

	/**
	 * @see ReportService#getAllReportProcessorConfigurations(boolean)
	 */
	public List<ReportProcessorConfiguration> getAllReportProcessorConfigurations(boolean includeRetired) {
		return reportDAO.getAllReportProcessorConfigurations(includeRetired);
	}
	
	/**
	 * 
	 * @see ReportService#getGlobalReportProcessorConfigurations()
	 */
	public List<ReportProcessorConfiguration> getGlobalReportProcessorConfigurations() {
		return reportDAO.getGlobalReportProcessorConfigurations();
	}

	/**
	 * @see ReportService#getReportProcessorConfigurations(Class)
	 */
	public List<ReportProcessorConfiguration> getReportProcessorConfigurations(Class<? extends ReportProcessor> processorType) {
		List<ReportProcessorConfiguration> ret = new ArrayList<ReportProcessorConfiguration>();
		for (ReportProcessorConfiguration p : getAllReportProcessorConfigurations(false)) {
			try {
				Class<?> clazz = Context.loadClass(p.getProcessorType());
				if (processorType.isAssignableFrom(clazz)) {
					ret.add(p);
				}
			}
			catch (Exception e) {
				log.warn("Error trying to load processor class " + p.getProcessorType(), e);
			}
		}
		return ret;
	}

	/**
	 * @see ReportService#purgeReportProcessorConfiguration(ReportProcessorConfiguration)
	 */
	public void purgeReportProcessorConfiguration(ReportProcessorConfiguration processorConfiguration) {
		reportDAO.purgeReportProcessorConfiguration(processorConfiguration);
	}
	
	//***** REPORTS *****
	
	/**
	 * @see ReportService#getReportDataFile(ReportRequest)
	 */
	public File getReportDataFile(ReportRequest request) {
		File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(ReportingConstants.REPORT_RESULTS_DIRECTORY_NAME);
		return new File(dir, request.getUuid() + ".reportdata.gz");
	}
	
	/**
	 * @see ReportService#getReportErrorFile(ReportRequest)
	 */
	public File getReportErrorFile(ReportRequest request) {
		File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(ReportingConstants.REPORT_RESULTS_DIRECTORY_NAME);
		return new File(dir, request.getUuid() + ".reporterror");
	}
	
	/**
	 * @see ReportService#getReportOutputFile(ReportRequest)
	 */
	public File getReportOutputFile(ReportRequest request) {
		File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(ReportingConstants.REPORT_RESULTS_DIRECTORY_NAME);
		return new File(dir, request.getUuid() + ".reportoutput");
	}
	
	/**
	 * @see ReportService#getReportOutputFile(ReportRequest)
	 */
	public File getReportLogFile(ReportRequest request) {
		File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(ReportingConstants.REPORT_RESULTS_DIRECTORY_NAME);
		return new File(dir, request.getUuid() + ".reportlog");
	}
	
	/**
	 * @see ReportService#queueReport(ReportRequest)
	 */
	@Transactional
	public ReportRequest queueReport(ReportRequest request) {
		
		if (request.getStatus() == null) {
			if (ObjectUtil.notNull(request.getSchedule())) {
				request.setStatus(Status.SCHEDULED);
				request.setPriority(Priority.NORMAL);
				logReportMessage(request, "Report Scheduled by " + ObjectUtil.getNameOfCurrentUser());
			}
			else {
				request.setStatus(Status.REQUESTED);
				request.setPriority(Priority.HIGHEST);
				logReportMessage(request, "Report Requested by " + ObjectUtil.getNameOfCurrentUser());
			}
		}
		
		request =  Context.getService(ReportService.class).saveReportRequest(request);
		
		Integer position = getPositionInQueue(request);
		if (position != null) {
			logReportMessage(request, "Report in queue at position " + position);
		}
		
		return request;
	}
	
	/**
	 * @see ReportService#getPositionInQueue(ReportRequest)
	 */
	@Transactional(readOnly=true)
	public Integer getPositionInQueue(ReportRequest request) {
		List<ReportRequest> l = getReportRequests(null, null, null, Status.REQUESTED);
		Collections.sort(l, new PriorityComparator());
		for (int i=0; i<l.size(); i++) {
			ReportRequest rr = l.get(i);
			if (rr.equals(request)) {
				return (i+1);
			}
		}
		return null;
	}
	
	/**
	 * @see ReportService#processNextQueuedReports()
	 */
	public void processNextQueuedReports() {
		runQueuedReportsTask.createAndRunTask();
	}
	
	/**
	 * @see ReportService#saveReport(Report, String)
	 */
	public Report saveReport(Report report, String description) {
		String reportRequestUuid = report.getRequest().getUuid();
		CachedReportData cachedData = persistCachedReportDataToDisk(reportRequestUuid);
		if (cachedData != null && !cachedData.isPersisted()) {
			throw new ReportingException("Unable to save Report due to error saving Report Data to disk");
		}
		ReportRequest request = Context.getService(ReportService.class).getReportRequest(report.getRequest().getId());
		request.setStatus(Status.SAVED);
		request.setDescription(description);
		Context.getService(ReportService.class).saveReportRequest(request);
		logReportMessage(request, "Report Saved");
		report.setRequest(request);
		return report;
	}
	
	/**
	 * @see ReportService#runReport(ReportRequest)
	 */
	public Report runReport(ReportRequest request) {
		
		// Start up a timer to check performance
		long startTime = System.currentTimeMillis();
		
		// Set the status to processing and save the request
		request.setStatus(Status.PROCESSING);
		request.setEvaluateStartDatetime(new Date());
		logReportMessage(request, "Starting to process report...");

		Context.flushSession(); // Ensure other threads can see updated request
		
		// Construct a new report object to return
		Report report = new Report(request);
		ReportDefinitionService rds = Context.getService(ReportDefinitionService.class);
		try {
			// Create a new Evaluation Context, setting the base cohort from the request
			Date evaluationDate = request.getEvaluationDate() == null ? new Date() : request.getEvaluationDate();
			EvaluationContext context = new EvaluationContext(evaluationDate);
			context.addContextValue(GENERATED_BY, ObjectUtil.getNameOfUser(request.getRequestedBy()));
			context.addContextValue(GENERATION_DATE, request.getRequestDate());
			
			if (request.getBaseCohort() != null) {
				logReportMessage(request, "Evaluating base Cohort....");
				try {
					Cohort baseCohort = Context.getService(CohortDefinitionService.class).evaluate(request.getBaseCohort(), context);
					context.setBaseCohort(baseCohort);
				} 
				catch (Exception ex) {
					throw new EvaluationException("baseCohort", ex);
				}
			}
		
			// Evaluate the Report Definition, any EvaluationException thrown by the next line can bubble up; wrapping it won't provide useful information
			logReportMessage(request, "Evaluating Report Data....");
			
			ReportData reportData = rds.evaluate(request.getReportDefinition(), context);
			request.setEvaluateCompleteDatetime(new Date());
			Context.flushSession(); // Ensure other threads can see updated request

			// Determine whether or not to render report data to bytes or to cache the raw data
			boolean renderReportDataToBytes = false;
			if (request.getRenderingMode() != null) {
				ReportRenderer renderer = request.getRenderingMode().getRenderer();
				renderReportDataToBytes = !(renderer instanceof InteractiveReportRenderer);
			}

			if (renderReportDataToBytes) {
				logReportMessage(request, "Generating Rendered Report....");
				ReportRenderer renderer = request.getRenderingMode().getRenderer();
				String argument = request.getRenderingMode().getArgument();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				renderer.render(reportData, argument, out);
				report.setRenderedOutput(out.toByteArray());
				request.setRenderCompleteDatetime(new Date());

				logReportMessage(request, "Writing the report output to disk");
				ReportUtil.writeByteArrayToFile(getReportOutputFile(report.getRequest()), report.getRenderedOutput());
			}
			else {
				logReportMessage(request, "Caching Report Results....");
				report.setReportData(reportData);
				cacheReportData(request.getUuid(), reportData);
			}
			request.setStatus(Status.COMPLETED);
		}
		catch (Throwable t) {
			request.setStatus(Status.FAILED);
			logReportMessage(request, "Report Evaluation Failed");
			try {
				StringWriter sw = new StringWriter();
				t.printStackTrace(new PrintWriter(sw));
				report.setErrorMessage(sw.toString());

				logReportMessage(request, "Writing the report error to disk");
				ReportUtil.writeStringToFile(getReportErrorFile(report.getRequest()), report.getErrorMessage());
			}
			catch (Exception e) {
				log.warn("Unable to log reporting error to file.", e);
			}
		}

		Context.flushSession(); // Ensure other threads can see updated request

		Context.getService(ReportService.class).saveReportRequest(request);
		
		// Find applicable global processors
		if (request.isProcessAutomatically()) {
			List<ReportProcessorConfiguration> processorsToRun = ReportUtil.getAvailableReportProcessorConfigurations(request, 
					ReportProcessorConfiguration.ProcessorMode.AUTOMATIC, 
					ReportProcessorConfiguration.ProcessorMode.ON_DEMAND_AND_AUTOMATIC);
			
			for (ReportProcessorConfiguration c : processorsToRun) {
				try {
					if ((request.getStatus() == Status.COMPLETED && c.getRunOnSuccess()) || (request.getStatus() == Status.FAILED && c.getRunOnError())) {
						logReportMessage(request, "Processing Report with " + c.getName() + "...");
						Class<?> processorType = Context.loadClass(c.getProcessorType());
						ReportProcessor processor = (ReportProcessor)processorType.newInstance();
						processor.process(report, c.getConfiguration());
					}
				}
				catch (Exception e) {
					log.warn("Report Processor Failed: " + c.getName(), e);
					logReportMessage(request, "Report Processor Failed: " + c.getName());
				}
			}
		}
		
		long endTime = System.currentTimeMillis();
		logReportMessage(request, "Report Generation Completed in " + (int)((endTime - startTime)/1000) + " seconds.");
		
		return report;
	}
	
	/**
	 * Loads the ReportData previously generated Report for the given ReportRequest, first checking the cache
	 */
	public ReportData loadReportData(ReportRequest request) {
		log.debug("Loading ReportData for ReportRequest");
		CachedReportData cachedData = reportCache.get(request.getUuid());
		if (cachedData != null) {
			return cachedData.getReportData();
		}
		try {
			long t1 = System.currentTimeMillis();
			File reportDataFile = getReportDataFile(request);
			if (reportDataFile.exists()) {
				String s = ReportUtil.readStringFromFile(reportDataFile);
				ReportData reportData = Context.getSerializationService().deserialize(s, ReportData.class, ReportingSerializer.class);
				long t2 = System.currentTimeMillis();
				log.info("Loaded and Deserialized ReportData from file in " + (int) ((t2 - t1) / 1000) + " seconds");
				return reportData;
			}
		}
		catch (Exception e) {
			log.warn("Failed to load ReportData from disk for request " + request + " due to " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Loads the Rendered Output for a previously generated Report for the given ReportRequest
	 */
	public byte[] loadRenderedOutput(ReportRequest request) {
		log.debug("Loading Rendered Output for ReportRequest");
		try {
			File outputFile = getReportOutputFile(request);
			if (outputFile.exists()) {
				return ReportUtil.readByteArrayFromFile(outputFile);
			}
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
			File errorFile = getReportErrorFile(request);
			if (errorFile != null) {
				return ReportUtil.readStringFromFile(errorFile);
			}
		}
		catch (Exception e) {
			log.warn("Failed to load Report Error from disk for request " + request + " due to " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Loads the logs for a Report for the given ReportRequest
	 */
	public List<String> loadReportLog(ReportRequest request) {
		log.debug("Loading Report Log for ReportRequest");
		try {
			File logFile = getReportLogFile(request);
			if (logFile != null) {
				return ReportUtil.readLinesFromFile(logFile);
			}
		}
		catch (Exception e) {
			log.warn("Failed to load Report Log from disk for request " + request + " due to " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Loads a previously generated Report for the given ReportRequest, first checking the cache
	 */
	public Report loadReport(ReportRequest request) {
		log.info("Loading Report for ReportRequest");
		Report report = new Report(request);
		report.setReportData(loadReportData(request));
		report.setRenderedOutput(loadRenderedOutput(request));
		report.setErrorMessage(loadReportError(request));
		return report;
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

        log.debug("In Delete Old Report Requests");

		if (ageInHoursToDelete <= 0) {
            log.warn("Non-positive number configured (" + ageInHoursToDelete + ") for " + ReportingConstants.GLOBAL_PROPERTY_DELETE_REPORTS_AGE_IN_HOURS + ". Not deleting any.");
			return;
		}

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -1*ageInHoursToDelete);
        Date now = new Date();

        log.debug("Checking for reports older than " + ageInHoursToDelete + " hours. Request date before " + cal.getTime());

        List<ReportRequest> oldRequests = getReportRequests(null, null, cal.getTime(), Status.COMPLETED, Status.FAILED);

        log.debug("Found " + oldRequests.size() + " requests that qualify");

		for (ReportRequest request : oldRequests) {
            log.debug("Checking request" + request.getUuid());

            int daysSinceRequest = DateUtil.getDaysBetween(request.getRequestDate(), now);
            log.debug("Days since request = " + daysSinceRequest + " and minimum days to preserve configured to " + request.getMinimumDaysToPreserve());

            if (request.getMinimumDaysToPreserve() == null || request.getMinimumDaysToPreserve() < daysSinceRequest) {
                log.info("Request qualifies for deletion.  Deleting: " + request.getUuid());
                try {
                    purgeReportRequest(request);
                }
                catch (Exception e) {
                    log.warn("Unable to delete old report request: " + request, e);
                }
            }
		}
    }
	
	/**
	 * @see ReportService#persistCachedReports()
	 */
	public synchronized void persistCachedReports() {
		Set<String> cachedRequests = reportCache.keySet();
		for (String reportRequestUuid : cachedRequests) {
			persistCachedReportDataToDisk(reportRequestUuid);
		}
		if (reportCache.size() > 0 && reportCache.size() >= ReportingConstants.GLOBAL_PROPERTY_MAX_CACHED_REPORTS()) {
			Iterator<String> i = reportCache.keySet().iterator();
			i.next();
			i.remove();
		}
    }
	
	/**
	 * @see ReportService#logReportMessage(ReportRequest, String)
	 */
	@Transactional(readOnly=true)
	public void logReportMessage(ReportRequest request, String message) {
		try {
			File f = getReportLogFile(request);
			String d = DateUtil.formatDate(new Date(), "EEE dd/MMM/yyyy HH:mm:ss z");
			ReportUtil.appendStringToFile(f, d + " | " + message);			
		}
		catch (Exception e) {
			log.warn("Unable to log report message to disk: " + message, e);
		}
	}
	
	//***** PRIVATE UTILITY METHODS *****
	
	/**
	 * @param requestUuid the uuid of the ReportRequest that was evaluated
	 * @param reportData the data to cache
	 */
	protected synchronized void cacheReportData(String requestUuid, ReportData reportData) {
		CachedReportData cachedData = new CachedReportData(reportData);
		reportCache.put(requestUuid, cachedData);
	}


	/**
	 * Saves the CachedReportData to disk
	 */
	protected CachedReportData persistCachedReportDataToDisk(String reportRequestUuid) {
		CachedReportData cachedData = reportCache.get(reportRequestUuid);
		if (cachedData != null) {
			if (cachedData.isPersisted()) {
				log.debug("Cached Data is already persisted, returning");
			} else {
				ReportRequest request = getReportRequestByUuid(reportRequestUuid);
				BufferedOutputStream out = null;
				try {
					Timer timer = Timer.start();
					File reportDataFile = getReportDataFile(request);
					log.info(timer.logInterval("About to serialize the ReportData to " + reportDataFile.getPath()));
					out = new BufferedOutputStream(new FileOutputStream(reportDataFile));
					ReportingSerializer serializer = (ReportingSerializer) Context.getSerializationService().getSerializer(ReportingSerializer.class);
					serializer.serializeToStream(cachedData.getReportData(), out);
					log.info(timer.logInterval("Serialized the report data to disk"));
					cachedData.setPersisted(true);
				} catch (Exception e) {
					log.warn("An error occurred writing report data to disk", e);
				} finally {
					IOUtils.closeQuietly(out);
				}
			}
		}
		return cachedData;
	}

	//***** PROPERTY ACCESS *****

	public ReportDAO getReportDAO() {
		return reportDAO;
	}

	public void setReportDAO(ReportDAO reportDAO) {
		this.reportDAO = reportDAO;
	}

	public ReportingTimerTask getRunQueuedReportsTask() {
		return runQueuedReportsTask;
	}

	public void setRunQueuedReportsTask(ReportingTimerTask runQueuedReportsTask) {
		this.runQueuedReportsTask = runQueuedReportsTask;
	}

	//***** INNER CLASS FOR CACHING *****

	private class CachedReportData {
		private boolean persisted = false;
		private ReportData reportData;

		public CachedReportData(ReportData reportData) {
			this.reportData = reportData;
			this.setPersisted(false);
		}

		public boolean isPersisted() {
			return persisted;
		}

		public void setPersisted(boolean persisted) {
			this.persisted = persisted;
		}

		public ReportData getReportData() {
			return reportData;
		}

		public void setReportData(ReportData reportData) {
			this.reportData = reportData;
		}
	}
}