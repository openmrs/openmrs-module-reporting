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
package org.openmrs.module.reporting.query.encounter.definition;

import org.openmrs.Encounter;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.query.BaseQuery;

/**
 * Cohort Definition-based Encounter Query
 */
public class PatientEncounterQuery extends BaseQuery<Encounter> implements EncounterQuery {

    public static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(required=true)
	private CohortDefinition patientQuery;
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public PatientEncounterQuery() {
		super();
	}
	
	/**
	 * Full Constructor
	 */
	public PatientEncounterQuery(CohortDefinition patientQuery) { 
		setPatientQuery(patientQuery);
	}

	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Encounter Patient Query";
	}


	
	//***** PROPERTY ACCESS *****
	
	/**
	 * @return the patientQuery
	 */
	public CohortDefinition getPatientQuery() {
		return patientQuery;
	}

	/**
	 * @param patientQuery the patientQuery to set
	 */
	public void setPatientQuery(CohortDefinition patientQuery) {
		this.patientQuery = patientQuery;
		this.setParameters(patientQuery.getParameters());
	}
}
