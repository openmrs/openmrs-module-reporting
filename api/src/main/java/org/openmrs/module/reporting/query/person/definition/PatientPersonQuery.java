/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.person.definition;

import org.openmrs.Person;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.query.BaseQuery;

/**
 * Cohort Definition-based Person Query
 */
public class PatientPersonQuery extends BaseQuery<Person> implements PersonQuery {

    public static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(required=true)
	private CohortDefinition patientQuery;
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public PatientPersonQuery() {
		super();
	}
	
	/**
	 * Full Constructor
	 */
	public PatientPersonQuery(CohortDefinition patientQuery) {
		setPatientQuery(patientQuery);
	}

	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Patient Person Query";
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
