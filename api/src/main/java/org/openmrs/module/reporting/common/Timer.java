/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.common;

/**
 * A utility class for recording the time it takes to perform operations
 */
public class Timer {
	
	//***** PROPERTIES *****
	private long startTime;
	private long lastInterval;

	//***** CONSTRUCTORS *****
	private Timer() {
		startTime = System.currentTimeMillis();
		lastInterval = startTime;
	}
	
	/**
	 * @return a new Timer initialized with the current time
	 */
	public static Timer start() {
		return new Timer();
	}
	
	/**
	 * Resets the timer to the current time, and returns the time since the timer started or was last checked
	 * @return the number of milliseconds since the start or last check
	 */
	public String logInterval(String message) {
		long newTime = System.currentTimeMillis();
		long overallDuration = newTime - startTime;
		long intervalDuration = newTime - lastInterval;
		lastInterval = newTime;
		return message + ": " + intervalDuration + " ms for interval; " + overallDuration + " ms overall";
	}
}
