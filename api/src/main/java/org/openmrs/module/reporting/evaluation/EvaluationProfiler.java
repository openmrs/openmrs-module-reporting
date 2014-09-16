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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.util.OpenmrsUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Class that can be used to profile the time a method takes to execute
 * Outputs TRACE level log messages with timing information.
 * To view those messages ensure that that the EvaluationProfiler logger is set to TRACE.
 */
public class EvaluationProfiler {

	protected static final Log log = LogFactory.getLog(EvaluationProfiler.class);
	public static final String EVALUATION_LOG_FILE_PREFIX = "evaluation-";

	public static final String BEFORE = "BEFORE";
	public static final String ERROR = "ERROR";
	public static final String AFTER = "AFTER";

	//***** INSTANCE VARIABLES *****

	private EvaluationContext context;
	private long overallStartTime;
	private long lastStartTime;
	private File logFile;

	//***** CONSTRUCTOR *****

	public EvaluationProfiler(EvaluationContext context) {
		this.context = context;
		this.overallStartTime = System.currentTimeMillis();
		this.lastStartTime = overallStartTime;
		this.logFile = new File(getLogDirectory(), EVALUATION_LOG_FILE_PREFIX + DateUtil.formatDate(new Date(overallStartTime), "yyyyMMdd") + ".log");
	}

	// ***** INSTANCE METHODS *****

	/**
	 * Logs the start of an event
	 */
	public void logBefore(String eventCode, String message) {
		log(BEFORE, eventCode, message, null);
	}

	/**
	 * Logs an error during an event
	 */
	public void logError(String eventCode, String message, Throwable t) {
		log(ERROR, eventCode, message + ": " + t.getMessage(), t);
	}

	/**
	 * Logs the end of an event
	 */
	public void logAfter(String eventCode, String message) {
		log(AFTER, eventCode, message, null);
	}

	/**
	 * @return the overall time elapsed from the time the profiler was instantiated until now
	 */
	public String getTimeElapsedFromStart() {
		return DateUtil.getTimeElapsed(System.currentTimeMillis() - overallStartTime);
	}

	/**
	 * Logs an event, including with log4j and to the report evaluation log file
	 */
	private synchronized void log(String when, String eventCode, String message, Throwable t) {
		long logTime = System.currentTimeMillis();
		try {
			StringBuilder sb = new StringBuilder();
			appendToLine(sb, Long.toString(context.getEvaluationId()));
			appendToLine(sb, DateUtil.formatDate(new Date(logTime), "yyyy-MM-dd HH:mm:ss.S"));
			appendToLine(sb, StringUtils.repeat(">", context.getEvaluationLevel()));
			appendToLine(sb, when);
			appendToLine(sb, eventCode);
			if (AFTER.equals(when)) {
				appendToLine(sb, DateUtil.getTimeElapsed(logTime - lastStartTime));
				appendToLine(sb, DateUtil.getTimeElapsed(logTime - overallStartTime));
			}
			else {
				if (BEFORE.equals(when)) {
					lastStartTime = logTime;
				}
				appendToLine(sb, "");
				appendToLine(sb, "");
			}
			sb.append(message);
			if (ERROR.equals(when) && log.isErrorEnabled()) {
				log.error(sb.toString(), t);
			}
			else if (log.isTraceEnabled()) {
				log.trace(sb.toString());
			}
			if (ReportingConstants.GLOBAL_PROPERTY_EVALUATION_LOGGER_ENABLED()) {
				PrintWriter out = null;
				try {
					out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
					out.println(sb.toString());
				}
				catch (Exception e) {
					log.debug("Unable to write message to file", e);
				}
				finally {
					IOUtils.closeQuietly(out);
				}
			}
		}
		catch (Exception e) {
			log.trace("An error occurred logging an evaluation event", e);
		}
	}

	/**
	 * Utility method to add new lines to the log file
	 */
	protected void appendToLine(StringBuilder sb, String text) {
		sb.append(text).append("\t");
	}

	//***** STATIC METHODS *****

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
}