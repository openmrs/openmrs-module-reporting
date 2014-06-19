/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.reporting.evaluation;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.util.OpenmrsUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class can be used throughout the reporting module codebase to log events that might be of interest
 * to an administrator who is tracking the progress of a particular evaluation, or who wants more information
 * as to the specific workings of how a definition is evaluated.
 * All calls to "logBefore" should be followed by a call to "logAfter" in a finally block, to ensure that proper
 * cleanup is done of thread local variables used by this construct and to avoid memory leaks
 */
public class EvaluationLogger {

	//***** STATIC VARIABLES *****

	public static final String EVALUATION_LOG_FILE_PREFIX = "evaluation-";
	protected static final Log log = LogFactory.getLog(EvaluationLogger.class);
	private static ThreadLocal<EvaluationLog> activeEvaluations = new ThreadLocal<EvaluationLog>();
	private static long nextEvaluationLoggerId = 1;

	//***** PUBLIC STATIC METHODS *****

	/**
	 * This method should be invoked just before the event that you wish to log information about.
	 * This method should never be used on it's own, but should always be called in conjunction with a
	 * subsequent call to logAfterEvent(String, String)
	 */
	public static void logBeforeEvent(String eventCode, String message) {
		if (ReportingConstants.GLOBAL_PROPERTY_EVALUATION_LOGGER_ENABLED()) {
			EvaluationLog logger = activeEvaluations.get();
			if (logger == null) {
				logger = new EvaluationLog();
				activeEvaluations.set(logger);
				writeToLog(createLogEntry("start", "EVALUATION_STARTED", ObjectUtil.format(Context.getAuthenticatedUser()), logger));
			}
			logger.incrementNumNestedEvaluations();
			logger.resetEventStartTime();
			writeToLog(createLogEntry("before", eventCode, message, logger));
		}
	}

	/**
	 * This method should be invoked just after the event that you wish to log information about.
	 * This method should never be used on it's own, but should always be called in conjunction with a
	 * prior call to logBeforeEvent(String, String)
	 */
	public static void logAfterEvent(String eventCode, String message) {
		if (ReportingConstants.GLOBAL_PROPERTY_EVALUATION_LOGGER_ENABLED()) {
			EvaluationLog logger = activeEvaluations.get();
			logger.decrementNumNestedEvaluations();
			writeToLog(createLogEntry("after", eventCode, message, logger));
			if (logger.getNumNestedEvaluations() == 0) {
				writeToLog(createLogEntry("end", "EVALUATION_COMPLETED", DateUtil.getTimeElapsed(logger.getOverallTimeElapsed()), logger));
				activeEvaluations.remove();
			}
		}
	}

	/**
	 * This returns the directory in which all Evaluation Logs are written
	 */
	public static File getLogDirectory() {
		File baseDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(ReportingConstants.REPORT_RESULTS_DIRECTORY_NAME);
		File dir = new File(baseDir, ReportingConstants.EVALUATION_LOG_DIRECTORY_NAME);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	//**** PROTECTED STATIC METHODS *****

	protected static String createLogEntry(String when, String eventCode, String message, EvaluationLog logger) {
		StringBuilder sb = new StringBuilder();
		sb.append(when).append("\t").append(eventCode).append("\t");
		sb.append(DateUtil.getTimeElapsed(logger.getEventTimeElapsed())).append("\t");
		sb.append(logger.getEventTimeElapsed()).append("\t");
		sb.append(logger.getOverallTimeElapsed()).append("\t");
		sb.append(message);
		return sb.toString();
	}

	protected static synchronized void writeToLog(String message) {
		EvaluationLog logger = activeEvaluations.get();
		StringBuilder s = new StringBuilder();
		s.append(logger.evaluationLoggerId).append("\t").append(formatDate(new Date())).append("\t").append(message);
		writeLineToFile(logger.getLogFile(), s.toString());
		log.trace(s.toString());
	}

	protected static synchronized long getNextEvaluationLoggerId() {
		return nextEvaluationLoggerId++;
	}

	protected static String formatDate(Date d) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		return df.format(d);
	}

	protected static void writeLineToFile(File outputFile, String message) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true)));
			out.println(message);
		}
		catch (Exception e) {
			log.debug("Unable to write message to file", e);
		}
		finally {
			IOUtils.closeQuietly(out);
		}
	}

	//***** Utility class to assist with tracking a particular evaluation progress *****

	public static class EvaluationLog {

		private Long evaluationLoggerId;
		private long overallStartTime;
		private long eventStartTime;
		private int numNestedEvaluations = 0;
		private String logFileName = "";

		public EvaluationLog() {
			evaluationLoggerId = getNextEvaluationLoggerId();
			overallStartTime = System.currentTimeMillis();
			resetEventStartTime();
			logFileName = EVALUATION_LOG_FILE_PREFIX + DateUtil.formatDate(new Date(eventStartTime), "yyyyMMdd") + ".log";
		}

		public Long getEvaluationLoggerId() {
			return evaluationLoggerId;
		}

		public long getOverallStartTime() {
			return overallStartTime;
		}

		public long getEventStartTime() {
			return eventStartTime;
		}

		public int getNumNestedEvaluations() {
			return numNestedEvaluations;
		}

		public void incrementNumNestedEvaluations() {
			this.numNestedEvaluations++;
		}

		public void decrementNumNestedEvaluations() {
			this.numNestedEvaluations--;
		}

		public String getLogFileName() {
			return logFileName;
		}

		public File getLogFile() {
			return new File(getLogDirectory(), logFileName);
		}

		public long getOverallTimeElapsed() {
			return System.currentTimeMillis() - overallStartTime;
		}

		public long getEventTimeElapsed() {
			return System.currentTimeMillis() - eventStartTime;
		}

		public void resetEventStartTime() {
			eventStartTime = System.currentTimeMillis();
		}
	}
}