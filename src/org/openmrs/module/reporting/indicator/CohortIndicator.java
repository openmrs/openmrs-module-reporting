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

import java.util.Map;

import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.aggregation.Aggregator;

/**
 * Cohort-based indicator
 */
public class CohortIndicator extends BaseIndicator {
	
    private static final long serialVersionUID = 1L;
    
    /**
     * Enumerated Indicator Types
     */
    public enum IndicatorType {
    	COUNT, FRACTION, LOGIC
    }
    
    //***** PROPERTIES *****
    
    private IndicatorType type = IndicatorType.COUNT;
    private Mapped<? extends CohortDefinition> cohortDefinition;
    private Mapped<? extends CohortDefinition> denominator;
    private Mapped<? extends CohortDefinition> locationFilter;
    private Class<? extends Aggregator> aggregator;
    private String logicExpression;

    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public CohortIndicator() {
    	super();
    	addParameter(ReportingConstants.LOCATION_PARAMETER);
    }

    
    /**
     * Public constructor with arguments.
     * @param name
     * @param description
     * @param cohortDefinition
     * @param logicCriteria
     * @param aggregator
     */
    public CohortIndicator(String name, String description, Mapped<? extends CohortDefinition> cohortDefinition, String logicExpression, Class<? extends Aggregator> aggregator) { 
    	super();
    	this.setName(name);
    	this.setDescription(description);
    	this.cohortDefinition = cohortDefinition;
    	this.logicExpression = logicExpression;
    	this.aggregator = aggregator;
    }
	
    //***** Methods *****
    
    public String toString() {
    	return getName();
    }
    
    //***** Property Access *****

    /**
	 * @return the type
	 */
	public IndicatorType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(IndicatorType type) {
		this.type = type;
	}
    
	/**
     * @return the cohortDefinition
     */
    public Mapped<? extends CohortDefinition> getCohortDefinition() {
    	return cohortDefinition;
    }

	/**
     * @param cohortDefinition the cohortDefinition to set
     */
    public void setCohortDefinition(Mapped<? extends CohortDefinition> cohortDefinition) {
    	this.cohortDefinition = cohortDefinition;
    }
    
    /**
     * @param cohortDefinition the cohortDefinition to set
     */
    public void setCohortDefinition(CohortDefinition cohortDefinition, Map<String, Object> mappings) {
    	this.cohortDefinition = new Mapped<CohortDefinition>(cohortDefinition, mappings);
    }
    
    /**
	 * @return the denominator
	 */
	public Mapped<? extends CohortDefinition> getDenominator() {
		return denominator;
	}

	/**
	 * @param denominator the denominator to set
	 */
	public void setDenominator(Mapped<? extends CohortDefinition> denominator) {
		this.denominator = denominator;
	}

    /**
     * @param denominator the denominator to set
     */
    public void setDenominator(CohortDefinition denominator, Map<String, Object> mappings) {
    	this.denominator = new Mapped<CohortDefinition>(denominator, mappings);
    }

	/**
	 * @return the locationFilter
	 */
	public Mapped<? extends CohortDefinition> getLocationFilter() {
		return locationFilter;
	}

	/**
	 * @param locationFilter the locationFilter to set
	 */
	public void setLocationFilter(Mapped<? extends CohortDefinition> locationFilter) {
		this.locationFilter = locationFilter;
	}

    /**
     * @param locationFilter the locationFilter to set
     */
    public void setLocationFilter(CohortDefinition locationFilter, Map<String, Object> mappings) {
    	this.locationFilter = new Mapped<CohortDefinition>(locationFilter, mappings);
    }

	/**
	 * @return the logicExpression
	 */
	public String getLogicExpression() {
		return logicExpression;
	}

	/**
	 * @param logicExpression the logicExpression to set
	 */
	public void setLogicExpression(String logicExpression) {
		this.logicExpression = logicExpression;
	}

	/**
     * @return the aggregator
     */
    public Class<? extends Aggregator> getAggregator() {
    	return aggregator;
    }
	
    /**
     * @param aggregator the aggregator to set
     */
    public void setAggregator(Class<? extends Aggregator> aggregator) {
    	this.aggregator = aggregator;
    }
}