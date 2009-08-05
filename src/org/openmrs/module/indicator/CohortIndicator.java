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
package org.openmrs.module.indicator;

import java.util.Map;
import java.util.UUID;

import org.openmrs.logic.LogicCriteria;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.indicator.aggregation.Aggregator;

/**
 * Cohort-based indicator
 */
public class CohortIndicator extends BaseIndicator {
	
    private static final long serialVersionUID = 1L;
    
    //***** PROPERTIES *****

    private Mapped<? extends CohortDefinition> cohortDefinition;
    private Class<? extends Aggregator> aggregator;
    private LogicCriteria logicCriteria;

    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public CohortIndicator() {
    	super();
    }

    
    /**
     * Public constructor with arguments.
     * @param name
     * @param description
     * @param cohortDefinition
     * @param logicCriteria
     * @param aggregator
     */
    public CohortIndicator(String name, String description, Mapped<? extends CohortDefinition> cohortDefinition, LogicCriteria logicCriteria, Class<? extends Aggregator> aggregator) { 
    	super();
    	this.setName(name);
    	this.setDescription(description);
    	this.cohortDefinition = cohortDefinition;
    	this.logicCriteria = logicCriteria;
    	this.aggregator = aggregator;

    	// TODO Need to remove this once we get a serialization working
    	this.setUuid(UUID.randomUUID().toString());
    }
	
    //***** Property Access *****

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
    public void setCohortDefinition(CohortDefinition cohortDefinition, Map<String, String> mappings) {
    	this.cohortDefinition = new Mapped<CohortDefinition>(cohortDefinition, mappings);
    }
    
    /**
     * @param cohortDefinition the cohortDefinition to set
     */
    public void setCohortDefinition(CohortDefinition cohortDefinition, String mappings) {
    	this.cohortDefinition = new Mapped<CohortDefinition>(cohortDefinition, mappings);
    }
	
    /**
     * @return the logicCriteria
     */
    public LogicCriteria getLogicCriteria() {
    	return logicCriteria;
    }
	
    /**
     * @param logicCriteria the logicCriteria to set
     */
    public void setLogicCriteria(LogicCriteria logicCriteria) {
    	this.logicCriteria = logicCriteria;
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