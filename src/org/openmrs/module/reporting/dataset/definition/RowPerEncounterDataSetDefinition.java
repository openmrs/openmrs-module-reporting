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

import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.column.definition.encounter.EncounterColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.encounter.EncounterPatientColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.patient.PatientColumnDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.idset.definition.encounter.EncounterIdSetDefinition;

/**
 * DataSetDefinition for Producing a DataSet that has one row per Encounter
 * @see DataSetDefinition
 */
public class RowPerEncounterDataSetDefinition extends RowPerObjectDataSetDefinition<EncounterColumnDefinition> {
	
    //***** PROPERTIES *****
    
    @ConfigurationProperty
	private Mapped<? extends CohortDefinition> patientFilter;
    
    @ConfigurationProperty
	private Mapped<? extends EncounterIdSetDefinition> encounterFilter;
	
	//***** INSTANCE METHODS *****

	// THE FOLLOWING ARE CONVENIENCE METHODS TO HIDE WRAPPING COLUMNS IN A JOIN COLUMN
	
	/**
	 * @param column the PatientColumn to add
	 */
	public void addPatientColumnDefinition(Mapped<PatientColumnDefinition> column) {
		addPatientColumnDefinition(column.getParameterizable(), column.getParameterMappings());
	}
	
	/**
	 * @param column the PatientColumn to add
	 */
	public void addPatientColumnDefinition(PatientColumnDefinition column) {
		addPatientColumnDefinition(column, new HashMap<String, Object>());
	}
	
	/**
	 * @param column the PatientColumn to add
	 */
	public void addPatientColumnDefinition(PatientColumnDefinition column, Map<String, Object> mappings) {
		addColumnDefinition(new EncounterPatientColumnDefinition(column), mappings);
	}
	
	/**
	 * @param column the PatientColumn to add
	 */
	public void addPatientColumnDefinition(PatientColumnDefinition column, String mappings) {
		addPatientColumnDefinition(column, ParameterizableUtil.createParameterMappings(mappings));
	}
	
    //***** PROPERTY ACCESS *****

	/**
	 * @return the patientFilter
	 */
	public Mapped<? extends CohortDefinition> getPatientFilter() {
		return patientFilter;
	}

	/**
	 * @param patientFilter the patientFilter to set
	 */
	public void setPatientFilter(Mapped<? extends CohortDefinition> patientFilter) {
		this.patientFilter = patientFilter;
	}

	/**
	 * @return the encounterFilter
	 */
	public Mapped<? extends EncounterIdSetDefinition> getEncounterFilter() {
		return encounterFilter;
	}

	/**
	 * @param encounterFilter the encounterFilter to set
	 */
	public void setEncounterFilter(Mapped<? extends EncounterIdSetDefinition> encounterFilter) {
		this.encounterFilter = encounterFilter;
	}
}