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
package org.openmrs.module.reporting.data.person.definition;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Problem List Data Definition
 */
@Localized("reporting.ProblemListPersonDataDefinition")
public class ProblemListPersonDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	public static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(required=false)
    private List<Concept> problemAddedConcepts;
    
    @ConfigurationProperty(required=false)
    private List<Concept> problemRemovedConcepts;

    /**
	 * Default Constructor
	 */
	public ProblemListPersonDataDefinition() {
		super();
	}
	
	/**
	 * Name only Constructor
	 */
	public ProblemListPersonDataDefinition(String name) {
		super(name);
	}
	
	/**
	 * Constructor to populate all properties only
	 */
	public ProblemListPersonDataDefinition(String name, List<Concept> problemAddedConcepts, List<Concept> problemRemovedConcepts) {
		this(name);
		this.problemAddedConcepts = problemAddedConcepts;
		this.problemRemovedConcepts = problemRemovedConcepts;
	}
	
    public List<Concept> getProblemAddedConcepts() {
    	return problemAddedConcepts;
    }

    public void setProblemAddedConcepts(List<Concept> problemAddedConcepts) {
    	this.problemAddedConcepts = problemAddedConcepts;
    }

    public List<Concept> getProblemRemovedConcepts() {
    	return problemRemovedConcepts;
    }
	
    public void setProblemRemovedConcepts(List<Concept> problemRemovedConcepts) {
    	this.problemRemovedConcepts = problemRemovedConcepts;
    }
    
    /** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return List.class;
	}
}
