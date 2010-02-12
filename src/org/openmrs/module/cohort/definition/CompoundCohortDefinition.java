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
package org.openmrs.module.cohort.definition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.PatientSetService.BooleanOperator;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.evaluation.parameter.Mapped;

/**
 * CohortDefinition that is the composition of a single BooleanOperator
 * and a List of other CohortDefinitions to evaluate
 */
public class CompoundCohortDefinition extends BaseCohortDefinition {
	
    private static final long serialVersionUID = 1L;
	protected transient final Log log = LogFactory.getLog(getClass());
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
	private BooleanOperator operator = BooleanOperator.AND;
	
	@ConfigurationProperty(required=true)
	private List<Mapped<CohortDefinition>> definitions = new ArrayList<Mapped<CohortDefinition>>();
	
	//***** CONSTRUCTORS *****
	
    /**
     * Default constructor
     */
	public CompoundCohortDefinition() {
		super();
	}
	
	/**
	 * Full constructor
	 * @param operator - The operator to apply
	 * @param definitions - The CohortDefinitions to compound
	 */
	public CompoundCohortDefinition(BooleanOperator operator, List<Mapped<CohortDefinition>> definitions) {
		this();
		this.operator = operator;
		this.definitions = definitions;
	}

	/**
	 * Full constructor
	 * @param operator - The operator to apply
	 * @param definitions - The CohortDefinitions to compound
	 */
	public CompoundCohortDefinition(BooleanOperator operator, Mapped<CohortDefinition>... definitions) {
		this();
		this.operator = operator;
		this.definitions = Arrays.asList(definitions);
	}
	
	public void addDefinition(Mapped<CohortDefinition> definition) {
		definitions.add(definition);
	}

	//***** PROPERTY ACCESS *****
	
    /**
     * @return the operator
     */
    public BooleanOperator getOperator() {
    	return operator;
    }
	
    /**
     * @param operator the operator to set
     */
    public void setOperator(BooleanOperator operator) {
    	this.operator = operator;
    }

    /**
     * @return the definitions
     */
    public List<Mapped<CohortDefinition>> getDefinitions() {
    	return definitions;
    }

    /**
     * @param definitions the definitions to set
     */
    public void setDefinitions(List<Mapped<CohortDefinition>> definitions) {
    	this.definitions = definitions;
    }
}
