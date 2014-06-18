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

	//***** STATIC *****

	protected static final Log log = LogFactory.getLog(EvaluationLogger.class);
	private static ThreadLocal<EvaluationLogger> activeEvaluations = new ThreadLocal<EvaluationLogger>();
	private static ThreadLocal<Integer> numNestedEvaluations = new ThreadLocal<Integer>();
	private static long nextEvaluationLoggerId = 1;

	public static void logBeforeEvent(String eventCode, String message) {
		EvaluationLogger logger = activeEvaluations.get();
		if (logger == null) {
			logger = new EvaluationLogger();
			activeEvaluations.set(logger);
			numNestedEvaluations.set(0);
			writeToLog(createLogEntry("start", "EVALUATION_STARTED", ObjectUtil.format(Context.getAuthenticatedUser()), logger));
		}
		numNestedEvaluations.set(numNestedEvaluations.get() + 1);
		logger.resetEventStartTime();
		writeToLog(createLogEntry("before", eventCode, message, logger));
	}

	public static void logAfterEvent(String eventCode, String message) {
		EvaluationLogger logger = activeEvaluations.get();
		int count = numNestedEvaluations.get() - 1;
		message = createLogEntry("after", eventCode, message, logger);
		if (count == 0) {
			writeToLog(message);
			writeToLog(createLogEntry("end", "EVALUATION_COMPLETED", logger.getOverallTime(), logger));
			numNestedEvaluations.remove();
			activeEvaluations.remove();
		}
		else {
			numNestedEvaluations.set(count);
			writeToLog(message);
		}
	}

	protected static String createLogEntry(String when, String eventCode, String message, EvaluationLogger logger) {
		StringBuilder sb = new StringBuilder();
		sb.append(when).append("\t").append(eventCode).append("\t");
		sb.append(logger.getEventTime()).append("\t");
		sb.append(logger.getOverallTime()).append("\t");
		sb.append(message);
		return sb.toString();
	}

	protected static synchronized void writeToLog(String message) {
		EvaluationLogger logger = activeEvaluations.get();
		StringBuilder s = new StringBuilder();
		s.append(logger.evaluationLoggerId).append("\t").append(formatDate(new Date())).append("\t").append(message);
		writeLineToFile(logger.getLogFile(), s.toString());
		log.trace(s.toString());
	}

	protected static synchronized long getNextEvaluationLoggerId() {
		return nextEvaluationLoggerId++;
	}

	public static File getLogDirectory() {
		File baseDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(ReportingConstants.REPORT_RESULTS_DIRECTORY_NAME);
		File dir = new File(baseDir, ReportingConstants.EVALUATION_LOG_DIRECTORY_NAME);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
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

	protected static String formatTime(long fromTime) {
		long diff = System.currentTimeMillis() - fromTime;
		StringBuilder sb = new StringBuilder();
		int hrs = (int) (diff / (1000*60*60));
		if (hrs > 0) {
			sb.append(hrs + "h ");
			diff = diff - (hrs * 1000*60*60);
		}
		int mins = (int) (diff / (1000*60));
		if (mins > 0) {
			sb.append(mins + "m ");
			diff = diff - (mins * 1000*60);
		}
		int secs = (int) (diff / 1000);
		if (secs > 0) {
			sb.append(secs + "s ");
			diff = diff - (secs * 1000);
		}
		sb.append(diff + " ms");
		return sb.toString();
	}

	//***** INSTANCE *****

	private Long evaluationLoggerId;
	private long overallStartTime;
	private long eventStartTime;
	private String logFileName = "";

	private EvaluationLogger() {
		evaluationLoggerId = getNextEvaluationLoggerId();
		overallStartTime = System.currentTimeMillis();
		resetEventStartTime();
		logFileName = "evaluation-" + DateUtil.formatDate(new Date(eventStartTime), "yyyyMMdd") + ".log";
	}

	public File getLogFile() {
		return new File(getLogDirectory(), logFileName);
	}

	public String getOverallTime() {
		return formatTime(overallStartTime);
	}

	public String getEventTime() {
		return formatTime(eventStartTime);
	}

	public void resetEventStartTime() {
		eventStartTime = System.currentTimeMillis();
	}
}