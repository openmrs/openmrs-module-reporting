/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
