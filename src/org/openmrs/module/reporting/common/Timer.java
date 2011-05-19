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
