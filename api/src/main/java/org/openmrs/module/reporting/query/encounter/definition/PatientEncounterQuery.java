/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
