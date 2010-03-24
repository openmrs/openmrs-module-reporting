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
package org.openmrs.module.reporting.cohort.definition.toreview;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * 
 */
public class DrugsCompletedCohortDefinition extends BaseCohortDefinition {

    private static final long serialVersionUID = 1L;
    
	//***** PROPERTIES *****
	@ConfigurationProperty(required=false)
	private List<Concept> drugSets;
		
	@ConfigurationProperty(required=false)
	private List<Drug> drugs;

	@ConfigurationProperty(required=false)
	private Date completedOnOrAfter;

	@ConfigurationProperty(required=false)
	private Date completedOnOrBefore;

	//***** CONSTRUCTORS *****

	/**
	 * Default constructor
	 */
	public DrugsCompletedCohortDefinition() {
		super();
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		StringBuilder ret = new StringBuilder();
		ret.append("Patients that enrolled or completed in program state(s).");		
		return ret.toString();
	}

	//***** PROPERTY ACCESS *****
	
    /**
     * @return the program
     */
    public List<Drug> getDrugs() {
    	return drugs;
    }

    /**
     * @param program the program to set
     */
    public void setDrugs(List<Drug> drugs) {
    	this.drugs = drugs;
    }

	public List<Concept> getDrugSets() {
		return drugSets;
	}

	public void setDrugSets(List<Concept> drugSets) {
		this.drugSets = drugSets;
	}    
    
	public Date getCompletedOnOrAfter() {
		return completedOnOrAfter;
	}

	public void setCompletedOnOrAfter(Date completedOnOrAfter) {
		this.completedOnOrAfter = completedOnOrAfter;
	}

	public Date getCompletedOnOrBefore() {
		return completedOnOrBefore;
	}

	public void setCompletedOnOrBefore(Date completedOnOrBefore) {
		this.completedOnOrBefore = completedOnOrBefore;
	}    
    
}
