/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.indicator.dimension;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Cohort-based dimension result
 */
public class CohortDimensionResult implements Evaluated<Dimension> {
	
    public static final long serialVersionUID = 1L;
    
    //***** PROPERTIES *****

    private CohortDimension dimension;
    private EvaluationContext context;   
    private Map<String, Cohort> optionCohorts;

    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public CohortDimensionResult() {
    	super();
    }
    
    /**
     * Full Constructor
     */
    public CohortDimensionResult(CohortDimension dimension, EvaluationContext context) {
    	this.dimension = dimension;
    	this.context = context;
    }
    
    //***** Property Access *****

	/**
	 * @see Evaluated#getDefinition()
	 */
	public CohortDimension getDefinition() {
		return dimension;
	}

	/**
	 * @return the dimension
	 */
	public CohortDimension getDimension() {
		return dimension;
	}

	/**
	 * @param dimension the dimension to set
	 */
	public void setDimension(CohortDimension dimension) {
		this.dimension = dimension;
	}

	/**
	 * @return the context
	 */
	public EvaluationContext getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(EvaluationContext context) {
		this.context = context;
	}

	/**
	 * @return the optionCohorts
	 */
	public Map<String, Cohort> getOptionCohorts() {
		if (optionCohorts == null) {
			optionCohorts = new HashMap<String, Cohort>();
		}
		return optionCohorts;
	}

	/**
	 * @param optionCohorts the optionCohorts to set
	 */
	public void setOptionCohorts(Map<String, Cohort> optionCohorts) {
		this.optionCohorts = optionCohorts;
	}
	
	/**
	 * Adds a Cohort Result for a Dimension Options
	 */
	public void addOptionCohort(String option, Cohort cohort) {
		getOptionCohorts().put(option, cohort);
	}
	
	/**
	 * Returns the Cohort for the given Dimension Option
	 * @param option
	 * @return
	 */
	public Cohort getCohort(String option) {
		return getOptionCohorts().get(option);
	}
}