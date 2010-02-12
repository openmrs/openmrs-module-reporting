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

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * 
 */
public class DrugsActiveCohortDefinition extends BaseCohortDefinition {

    private static final long serialVersionUID = 1L;
    
	//***** PROPERTIES *****

	@ConfigurationProperty(required=false)
	private List<Concept> drugSets;

	@ConfigurationProperty(required=false)
	private List<Drug> drugs;

	@ConfigurationProperty(required=false)
	private List<Drug> excludeDrugs;	
	
	@ConfigurationProperty(required=false)
	private Date asOfDate;

	//***** CONSTRUCTORS *****

	/**
	 * Default constructor
	 */
	public DrugsActiveCohortDefinition() {
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
	
    public List<Drug> getDrugs() {
    	return drugs;
    }
    
    public void setDrugs(List<Drug> drugs) {
    	this.drugs = drugs;
    }
    
	public List<Concept> getDrugSets() {
		return drugSets;
	}

	public void setDrugSets(List<Concept> drugSets) {
		this.drugSets = drugSets;
	}

	public List<Drug> getExcludeDrugs() {
		return excludeDrugs;
	}

	public void setExcludeDrugs(List<Drug> excludeDrugs) {
		this.excludeDrugs = excludeDrugs;
	}
    
	public Date getAsOfDate() {
		return asOfDate;
	}

	public void setAsOfDate(Date asOfDate) {
		this.asOfDate = asOfDate;
	}
    
}
