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
package org.openmrs.module.reporting.cohort.definition;

import java.util.List;

import org.openmrs.PatientIdentifierType;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class PatientIdentifierCohortDefinition extends BaseCohortDefinition {

    private static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(required=false)
	private String startsWith;

    @ConfigurationProperty(required=false)
	private String matchRegex;
	
	@ConfigurationProperty(required=false)
	private List<PatientIdentifierType> patientIdentifierTypes;
	
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public PatientIdentifierCohortDefinition() {
		super();
	}
	
	public PatientIdentifierCohortDefinition(String matchRegex, String startsWith, List<PatientIdentifierType> patientIdentifierTypes) { 
		super();
		this.matchRegex = matchRegex;
		this.startsWith = startsWith;
		this.patientIdentifierTypes = patientIdentifierTypes;
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer("Patients with ");
		if (matchRegex != null) {
			buffer.append("identifier that matches the regular expression " + matchRegex);
		} 
		if (startsWith != null) {
			buffer.append("identifier that starts with the string " + startsWith);
		} 
		if (patientIdentifierTypes != null) {
			buffer.append("identifier that matches the identifier type(s) in " + patientIdentifierTypes);
		}	
		buffer.append("");
		
		return buffer.toString();
	}

	public String getStartsWith() {
		return this.startsWith;
	}
	
	public void setStartsWith(String startsWith) { 
		this.startsWith = startsWith;
	}
		
	public String getMatchRegex() {
		return matchRegex;
	}

	public void setMatchRegex(String matchRegex) {
		this.matchRegex = matchRegex;
	}

	public List<PatientIdentifierType> getPatientIdentifierTypes() {
		return patientIdentifierTypes;
	}

	public void setPatientIdentifierTypes(
			List<PatientIdentifierType> patientIdentifierTypes) {
		this.patientIdentifierTypes = patientIdentifierTypes;
	}
	
	
	
	
	
}
