package org.openmrs.module.reporting.indicator;

import org.openmrs.Cohort;

/**
 * 
 * Simple abstract class to extend if you want a custom indicator type (or other) to be able to return a Cohort
 * This is useful for allowing custom indicator types, for example, to be able to use default Reporting UI
 * 
 * @author dthomas
 *
 */
public abstract class CohortReturnable {

	/**
	 * returns a Cohort
	 * @return 
	 */
	public abstract Cohort getCohort();
	
}
