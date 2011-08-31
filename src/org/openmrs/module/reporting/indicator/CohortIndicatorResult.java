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
package org.openmrs.module.reporting.indicator;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.module.reporting.common.Fraction;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.indicator.CohortIndicator.IndicatorType;
import org.openmrs.module.reporting.indicator.aggregation.AggregationUtil;
import org.openmrs.module.reporting.indicator.aggregation.Aggregator;
import org.openmrs.module.reporting.indicator.aggregation.CountAggregator;

/**
 * Cohort-based indicator
 */
public class CohortIndicatorResult implements IndicatorResult {
	
    public static final long serialVersionUID = 1L;
    
    //***** PROPERTIES *****

    private CohortIndicator indicator;
    private EvaluationContext context;
    
    private Cohort cohort;
    private Cohort denominatorCohort;
    private Map<Integer, Number> logicResults = new HashMap<Integer, Number>(); // patient id -> logic value

    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public CohortIndicatorResult() {
    	super();
    }
    
    //***** INSTANCE METHODS *****
    
    public static Number getResultValue(CohortIndicatorResult cohortIndicatorResult, Cohort...filters) {
    	
    	IndicatorType type = cohortIndicatorResult.getDefinition().getType();
    	Cohort numerator = cohortIndicatorResult.getCohort();
    	Cohort denominator = cohortIndicatorResult.getDenominatorCohort();
    	Map<Integer, Number> logicVals = new HashMap<Integer, Number>(cohortIndicatorResult.getLogicResults());
    	
    	// Reduce each of the result cohorts as needed based on the filter Cohorts
    	if (filters != null) {
	    	for (Cohort filter : filters) {
	    		if (filter != null) {
		    		numerator = Cohort.intersect(numerator, filter);
		    		if (type == IndicatorType.FRACTION) {
		    			denominator = Cohort.intersect(denominator, filter);
		    		}
		    		else if (type == IndicatorType.LOGIC) {
		    			logicVals.keySet().retainAll(filter.getMemberIds());
		    		}
	    		}
	    	}
    	}
    	
    	// Return the appropriate result, given the IndicatorType
    	if (type == IndicatorType.FRACTION) {
    		int n = numerator.getSize();
    		int d = denominator.getSize();
    		return new Fraction(n, d);
    	}
    	else if (type == IndicatorType.LOGIC) {
    		Class<? extends Aggregator> aggregator = cohortIndicatorResult.getDefinition().getAggregator();
        	if (aggregator == null) {
        		aggregator = CountAggregator.class;
        	}
        	return AggregationUtil.aggregate(logicVals.values(), aggregator);
    	}
    	else { // Assume IndicatorType.COUNT
    		return numerator.getSize();
    	}
    }
    
    /**
     * @see IndicatorResult#getValue()
     */
    public Number getValue() {
    	return CohortIndicatorResult.getResultValue(this);
    }
    
	/** 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Number value = getValue();
		return (value == null ? "null" : value.toString());
	}
    
    //***** Property Access *****

	/**
	 * @see Evaluated#getDefinition()
	 */
	public CohortIndicator getDefinition() {
		return indicator;
	}

	/**
	 * @return the indicator
	 */
	public CohortIndicator getIndicator() {
		return indicator;
	}

	/**
	 * @param indicator the indicator to set
	 */
	public void setIndicator(CohortIndicator indicator) {
		this.indicator = indicator;
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
	 * @return the cohort
	 */
	public Cohort getCohort() {
		return cohort;
	}

	/**
	 * @param cohort the cohort to set
	 */
	public void setCohort(Cohort cohort) {
		this.cohort = cohort;
	}

	/**
	 * @return the denominatorCohort
	 */
	public Cohort getDenominatorCohort() {
		return denominatorCohort;
	}

	/**
	 * @param denominatorCohort the denominatorCohort to set
	 */
	public void setDenominatorCohort(Cohort denominatorCohort) {
		this.denominatorCohort = denominatorCohort;
	}

	/**
	 * @return the logicResults
	 */
	public Map<Integer, Number> getLogicResults() {
		if (logicResults == null) {
			logicResults = new HashMap<Integer, Number>();
		}
		return logicResults;
	}

	/**
	 * @param logicResults the logicResults to set
	 */
	public void setLogicResults(Map<Integer, Number> logicResults) {
		this.logicResults = logicResults;
	}

	/**
	 * @param patientId the patientId for which to add a logic result
	 * @param logicResult the logic result to add
	 */
	public void addLogicResult(Integer patientId, Number logicResult) {
		getLogicResults().put(patientId, logicResult);
	}
}