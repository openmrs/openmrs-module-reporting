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