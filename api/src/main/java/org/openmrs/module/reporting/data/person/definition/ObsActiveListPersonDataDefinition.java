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

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.ObsActiveList;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Observation-based Active List Data Definition
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.ObsActiveListPersonDataDefinition")
public class ObsActiveListPersonDataDefinition extends BaseDataDefinition implements PersonDataDefinition {

	public static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
    private List<Concept> startingConcepts;
    
    @ConfigurationProperty(required=false)
    private List<Concept> endingConcepts;

	//****** CONSTRUCTORS ******
    
    /**
	 * Default Constructor
	 */
	public ObsActiveListPersonDataDefinition() {
		super();
	}
	
	/**
	 * Name only Constructor
	 */
	public ObsActiveListPersonDataDefinition(String name) {
		super(name);
	}
	
	/**
	 * Constructor to populate all properties only
	 */
	public ObsActiveListPersonDataDefinition(String name, List<Concept> startingConcepts, List<Concept> endingConcepts) {
		this(name);
		this.startingConcepts = startingConcepts;
		this.endingConcepts = endingConcepts;
	}
	
	//***** INSTANCE METHODS *****
	
    /** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return ObsActiveList.class;
	}
	
	//****** PROPERTY ACCESS ******

	/**
	 * @return the startingConcepts
	 */
	public List<Concept> getStartingConcepts() {
		return startingConcepts;
	}

	/**
	 * @param startingConcepts the startingConcepts to set
	 */
	public void setStartingConcepts(List<Concept> startingConcepts) {
		this.startingConcepts = startingConcepts;
	}
	
	/**
	 * @param c the concept to add to the starting concepts
	 */
	public void addStartingConcept(Concept c) {
		if (startingConcepts == null) {
			startingConcepts = new ArrayList<Concept>();
		}
		startingConcepts.add(c);
	}

	/**
	 * @return the endingConcepts
	 */
	public List<Concept> getEndingConcepts() {
		return endingConcepts;
	}

	/**
	 * @param endingConcepts the endingConcepts to set
	 */
	public void setEndingConcepts(List<Concept> endingConcepts) {
		this.endingConcepts = endingConcepts;
	}
	
	/**
	 * @param c the concept to add to the ending concepts
	 */
	public void addEndingConcept(Concept c) {
		if (endingConcepts == null) {
			endingConcepts = new ArrayList<Concept>();
		}
		endingConcepts.add(c);
	}
}
