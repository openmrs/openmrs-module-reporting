package org.openmrs.module.reporting.report.service;

import org.apache.commons.io.FileUtils;
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
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
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
import org.openmrs.module.reporting.report.task.RunQueuedReportsTask;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.reporting.serializer.ReportingSerializer;
import org.openmrs.util.HandlerUtil;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Base Implementation of the ReportService API
 */
public class ReportServiceImpl extends BaseOpenmrsService implements ReportService {

	private static final String REPORT_RESULTS_DIR = "REPORT_RESULTS";
	public static final String GENERATED_BY = "generatedBy";
	public static final String GENERATION_DATE = "generationDate";
	
	// Logger
	private transient Log log = LogFactory.getLog(this.getClass());

	// Private variables
	private ReportDAO reportDAO;
	private TaskExecutor taskExecutor;
	private Map<String, Report> reportCache = new LinkedHashMap<String, Report>();
    private Map<String, Mapped<ReportDefinition>> inMemoryReportDefinitions = new HashMap<String, Mapped<ReportDefinition>>(); // by ReportRequest.uuid
		
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
	 * @see ReportService#getReportRequests(ReportDefinition, Date, Date, Status)
	 */
	@Transactional(readOnly=true)
	public List<ReportRequest> getReportRequests(ReportDefinition reportDefinition, Date requestOnOrAfter, Date requestOnOrBefore, Status...statuses) {
		return getReportRequests(reportDefinition, requestOnOrAfter, requestOnOrBefore, null, statuses);
	}

	/**
	 * @see ReportService#getReportRequests(ReportDefinition, Date, Date, Integer, Status)
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
			reportsTask.cancelCurrentlyRunningReportingTask();
		}
		reportDAO.purgeReportRequest(request);
		reportCache.remove(request.getUuid());
        inMemoryReportDefinitions.remove(request.getUuid());
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
	 * @see ReportService#getReportProcessorConfigurations(ReportDefinition, Date, Date, Status)
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
	 * @see ReportService#getReportOutputFile(ReportRequest)
	 */
	public File getReportLogFile(ReportRequest request) {
		File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(REPORT_RESULTS_DIR);
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

        if (request.getReportDefinition().getParameterizable().getId() == null) {
            inMemoryReportDefinitions.put(request.getUuid(), request.getReportDefinition());
            request.setReportDefinition(null);
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
		taskExecutor.execute(new RunQueuedReportsTask());
	}
	
	/**
	 * @see ReportService#saveReport(Report, String)
	 */
	public Report saveReport(Report report, String description) {
		boolean isPersisted = persistReportToDisk(report);
		if (isPersisted) {
			ReportRequest request = Context.getService(ReportService.class).getReportRequest(report.getRequest().getId());
			request.setStatus(Status.SAVED);
			request.setDescription(description);
			Context.getService(ReportService.class).saveReportRequest(request);
			logReportMessage(request, "Report Saved");
			report.setRequest(request);
			return report;
		}
		else {
			throw new ReportingException("Unable to save Report due to error saving Report to disk");
		}
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

            Mapped<ReportDefinition> reportDefinition = request.getReportDefinition();
            if (reportDefinition == null) {
                reportDefinition = inMemoryReportDefinitions.remove(request.getUuid());
                if (reportDefinition == null) {
                    throw new IllegalStateException("Tried to run a queued ReportRequest on an in-memory ReportDefinition, but could not find the in-memory definition. Probably the server or reporting module was reloaded after queuing the request.");
                }
            }

            ReportData reportData = rds.evaluate(reportDefinition, context);
			report.setReportData(reportData);
			request.setEvaluateCompleteDatetime(new Date());

			Context.flushSession(); // Ensure other threads can see updated request
			
			// Render the Report if appropriate
			if (request.getRenderingMode() != null) {
				ReportRenderer renderer = request.getRenderingMode().getRenderer();
				String argument = request.getRenderingMode().getArgument();
				if (!(renderer instanceof InteractiveReportRenderer)) {
					logReportMessage(request, "Generating Rendered Report....");
					ByteArrayOutputStream out = new ByteArrayOutputStream();
		            renderer.render(reportData, argument, out);
		            report.setRenderedOutput(out.toByteArray());
		            request.setRenderCompleteDatetime(new Date());
	            }
			}
			request.setStatus(Status.COMPLETED);
            logReportMessage(request, "Completed Evaluation and Rendering");
		}
		catch (Throwable t) {
			request.setStatus(Status.FAILED);
			logReportMessage(request, "Report Evaluation Failed");
			try {
				StringWriter sw = new StringWriter();
				t.printStackTrace(new PrintWriter(sw));
				report.setErrorMessage(sw.toString());
			}
			catch (Exception e) {
				log.warn("Unable to log reporting error to file.", e);
			}
		}

		Context.flushSession(); // Ensure other threads can see updated request
			
		// Cache the report
		logReportMessage(request, "Storing Report Results....");
		cacheReport(report);
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
	 * @see ReportService#getCachedReports()
	 */
	public Map<String, Report> getCachedReports() {
		return new LinkedHashMap<String, Report>(reportCache);
	}
	
	/**
	 * Loads the ReportData previously generated Report for the given ReportRequest, first checking the cache
	 */
	public ReportData loadReportData(ReportRequest request) {
		log.debug("Loading ReportData for ReportRequest");
		Report report = reportCache.get(request.getUuid());
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
		Report report = reportCache.get(request.getUuid());
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
	 * Loads the logs for a Report for the given ReportRequest
	 */
	public List<String> loadReportLog(ReportRequest request) {
		log.debug("Loading Report Log for ReportRequest");
		try {
			return ReportUtil.readLinesFromFile(getReportLogFile(request));
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
		Report report = reportCache.get(request.getUuid());
		if (report == null) {
			report = new Report(request);
			report.setReportData(loadReportData(request));
			report.setRenderedOutput(loadRenderedOutput(request));
			report.setPersisted(true);
			cacheReport(report);
		}
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
		if (ageInHoursToDelete <= 0) {
			return;
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -1*ageInHoursToDelete);
		for (ReportRequest request : getReportRequests(null, null, cal.getTime(), Status.COMPLETED,Status.FAILED)) {
			try {
				purgeReportRequest(request);
			}
			catch (Exception e) {
				log.warn("Unable to delete old report request: " + request, e);
			}
		}
    }
	
	/**
	 * @see ReportService#persistCachedReports()
	 */
	public synchronized void persistCachedReports() {
		for (Report r : reportCache.values()) {
			if (!r.isPersisted()) {
				persistReportToDisk(r);
				r.setPersisted(true);
			}
		}
		if (reportCache.size() >= ReportingConstants.GLOBAL_PROPERTY_MAX_CACHED_REPORTS()) {
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
	 * @param report the Report to cache
	 */
	protected synchronized void cacheReport(Report report) {
		reportCache.put(report.getRequest().getUuid(), report);
	}
	
	/**
	 * Saves the passed Report to disk within 3 separate files containing report data, output, and errors
	 * @return true if the report was successfully persisted to disk, false otherwise
	 */
	protected boolean persistReportToDisk(Report report) {
		
		boolean success = true;
		Timer timer = Timer.start();
		
		// Serialize the raw data to file
		try {
			String serializedData = Context.getSerializationService().serialize(report.getReportData(), ReportingSerializer.class);
			log.info(timer.logInterval("Serialized the ReportData"));
			ReportUtil.writeStringToFile(getReportDataFile(report.getRequest()), serializedData);
			log.info(timer.logInterval("Persisted the report data to disk"));
		}
		catch (Exception e) {
			success = false;
			log.warn("An error occurred writing report data to disk", e);
		}
		
		// Write the output to file
		if (report.getRenderedOutput() != null) {
			try {
				ReportUtil.writeByteArrayToFile(getReportOutputFile(report.getRequest()), report.getRenderedOutput());
				log.info(timer.logInterval("Persisted the report output to disk"));
			}
			catch (Exception e) {
				success = false;
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
				success = false;
				log.warn("An error occurred writing report error to disk", e);
			}
		}
		
		if (success) {
			report.setPersisted(true);
		}
		
		return success;
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
	 * @return the taskExecutor
	 */
	public TaskExecutor getTaskExecutor() {
		return taskExecutor;
	}

	/**
	 * @param taskExecutor the taskExecutor to set
	 */
	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}
}