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

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.reporting.dataset.column.definition.encounter.EncounterColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.obs.ObsColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.obs.ObsEncounterColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.obs.ObsPatientColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.patient.PatientColumnDefinition;
import org.openmrs.module.reporting.dataset.filter.Filter;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;

/**
 * DataSetDefinition for Producing a DataSet that has one row per Encounter
 * @see DataSetDefinition
 */
public class RowPerObsDataSetDefinition extends RowPerObjectDataSetDefinition<ObsColumnDefinition> {
	
    //***** PROPERTIES *****
    
    @ConfigurationProperty
	private Mapped<? extends Filter<Patient>> patientFilter;
    
    @ConfigurationProperty
	private Mapped<? extends Filter<Encounter>> encounterFilter;
    
    @ConfigurationProperty
	private Mapped<? extends Filter<Obs>> obsFilter;
	
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
		addColumnDefinition(new ObsPatientColumnDefinition(column), mappings);
	}
	
	/**
	 * @param column the PatientColumn to add
	 */
	public void addPatientColumnDefinition(PatientColumnDefinition column, String mappings) {
		addPatientColumnDefinition(column, ParameterizableUtil.createParameterMappings(mappings));
	}
	
	/**
	 * @param column the PatientColumn to add
	 */
	public void addEncounterColumnDefinition(Mapped<EncounterColumnDefinition> column) {
		addEncounterColumnDefinition(column.getParameterizable(), column.getParameterMappings());
	}
	
	/**
	 * @param column the PatientColumn to add
	 */
	public void addEncounterColumnDefinition(EncounterColumnDefinition column) {
		addEncounterColumnDefinition(column, new HashMap<String, Object>());
	}
	
	/**
	 * @param column the PatientColumn to add
	 */
	public void addEncounterColumnDefinition(EncounterColumnDefinition column, Map<String, Object> mappings) {
		addColumnDefinition(new ObsEncounterColumnDefinition(column), mappings);
	}
	
	/**
	 * @param column the PatientColumn to add
	 */
	public void addEncounterColumnDefinition(EncounterColumnDefinition column, String mappings) {
		addEncounterColumnDefinition(column, ParameterizableUtil.createParameterMappings(mappings));
	}
	
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

	/**
	 * @return the encounterFilter
	 */
	public Mapped<? extends Filter<Encounter>> getEncounterFilter() {
		return encounterFilter;
	}

	/**
	 * @param encounterFilter the encounterFilter to set
	 */
	public void setEncounterFilter(Mapped<? extends Filter<Encounter>> encounterFilter) {
		this.encounterFilter = encounterFilter;
	}

	/**
	 * @return the obsFilter
	 */
	public Mapped<? extends Filter<Obs>> getObsFilter() {
		return obsFilter;
	}

	/**
	 * @param obsFilter the obsFilter to set
	 */
	public void setObsFilter(Mapped<? extends Filter<Obs>> obsFilter) {
		this.obsFilter = obsFilter;
	}
}