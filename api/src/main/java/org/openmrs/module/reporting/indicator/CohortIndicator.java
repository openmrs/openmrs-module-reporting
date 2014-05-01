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
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.indicator.aggregation.Aggregator;

/**
 * Cohort-based indicator
 */
@Localized("reporting.CohortIndicator")
public class CohortIndicator extends BaseIndicator {
	
    public static final long serialVersionUID = 1L;
    
    /**
     * Enumerated Indicator Types
     */
    public enum IndicatorType {
    	COUNT, FRACTION, LOGIC
    }
    
    //***** PROPERTIES *****
    
    @ConfigurationProperty
    private IndicatorType type = IndicatorType.COUNT;
    
    @ConfigurationProperty
    private Mapped<? extends CohortDefinition> cohortDefinition;
    
    @ConfigurationProperty
    private Mapped<? extends CohortDefinition> denominator;
    
    @ConfigurationProperty
    private Mapped<? extends CohortDefinition> locationFilter;
    
    @ConfigurationProperty
    private Class<? extends Aggregator> aggregator;

	@ConfigurationProperty
	private Mapped<? extends PatientDataDefinition> dataToAggregate;

	@Deprecated
    @ConfigurationProperty
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
     * Default Constructor with name
     */
    public CohortIndicator(String name) {
    	this();
    	setName(name);
    }
    
    //***** FACTORY METHODS *****
    
    /**
     * Constructs a new Count Indicator
     */
    public static CohortIndicator newCountIndicator(String name,
    												Mapped<? extends CohortDefinition> cohortDefinition, 
    												Mapped<? extends CohortDefinition> locationFilter) {
    	CohortIndicator ci = new CohortIndicator(name);
    	ci.setType(IndicatorType.COUNT);
    	ci.setCohortDefinition(cohortDefinition);
    	ci.setLocationFilter(locationFilter);
    	return ci;
    }
    
    /**
     * Constructs a new Fraction Indicator
     */
    public static CohortIndicator newFractionIndicator(String name,
    												   Mapped<? extends CohortDefinition> numerator, 
    												   Mapped<? extends CohortDefinition> denominator, 
    												   Mapped<? extends CohortDefinition> locationFilter) {
    	CohortIndicator ci = new CohortIndicator(name);
    	ci.setType(IndicatorType.FRACTION);
    	ci.setCohortDefinition(numerator);
    	ci.setDenominator(denominator);
    	ci.setLocationFilter(locationFilter);
    	return ci;
    }
    
    /**
     * Constructs a new Logic Indicator
     */
	@Deprecated
    public static CohortIndicator newLogicIndicator(String name,
    												Mapped<? extends CohortDefinition> cohortDefinition,  
    												Mapped<? extends CohortDefinition> locationFilter,
    												Class<? extends Aggregator> aggregator,
    												String logicExpression) {
    	CohortIndicator ci = new CohortIndicator(name);
    	ci.setType(IndicatorType.LOGIC);
    	ci.setCohortDefinition(cohortDefinition);
    	ci.setLocationFilter(locationFilter);
    	ci.setAggregator(aggregator);
    	ci.setLogicExpression(logicExpression);
    	return ci;
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
     * @param cohortDefinition the cohortDefinition to set
     */
    public void setCohortDefinition(CohortDefinition cohortDefinition, String mappings) {
    	Map<String, Object> m = ParameterizableUtil.createParameterMappings(mappings);
    	setCohortDefinition(cohortDefinition, m);
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
     * @param denominator the denominator to set
     */
    public void setDenominator(CohortDefinition denominator, String mappings) {
    	Map<String, Object> m = ParameterizableUtil.createParameterMappings(mappings);
    	setDenominator(denominator, m);
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
     * @param locationFilter the locationFilter to set
     */
    public void setLocationFilter(CohortDefinition locationFilter, String mappings) {
    	Map<String, Object> m = ParameterizableUtil.createParameterMappings(mappings);
    	setLocationFilter(locationFilter, m);
    }

	/**
	 * @return the logicExpression
	 */
	@Deprecated
	public String getLogicExpression() {
		return logicExpression;
	}

	/**
	 * @param logicExpression the logicExpression to set
	 */
	@Deprecated
	public void setLogicExpression(String logicExpression) {
		this.logicExpression = logicExpression;
	}

	/**
	 * @return the dataToAggregate
	 */
	public Mapped<? extends PatientDataDefinition> getDataToAggregate() {
		return dataToAggregate;
	}

	/**
	 * @param dataToAggregate the dataToAggregate to set
	 */
	public void setDataToAggregate(Mapped<? extends PatientDataDefinition> dataToAggregate) {
		this.dataToAggregate = dataToAggregate;
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