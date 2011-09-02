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
package org.openmrs.module.reporting.dataset.definition;

import org.openmrs.Patient;
import org.openmrs.module.reporting.dataset.column.definition.patient.PatientColumnDefinition;
import org.openmrs.module.reporting.dataset.filter.Filter;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * DataSetDefinition for Producing a DataSet that has one row per Patient
 * @see DataSetDefinition
 */
public class RowPerPatientDataSetDefinition extends RowPerObjectDataSetDefinition<PatientColumnDefinition> {
	
    //***** PROPERTIES *****
    
    @ConfigurationProperty
	private Mapped<? extends Filter<Patient>> patientFilter;
    
    //***** PROPERTY ACCESS *****

	/**
	 * @return the patientFilter
	 */
	public Mapped<? extends Filter<Patient>> getPatientFilter() {
		return patientFilter;
	}

	/**
	 * @param patientFilter the patientFilter to set
	 */
	public void setPatientFilter(Mapped<? extends Filter<Patient>> patientFilter) {
		this.patientFilter = patientFilter;
	}
}