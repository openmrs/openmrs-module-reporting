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
package org.openmrs.module.reporting.data.patient.definition;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.ScriptingLanguage;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * A patient data definition made by performing calculations based on multiple person/patient data
 * definitions
 */
@Localized("reporting.ScriptedCompositionPatientDataDefinition")
public class ScriptedCompositionPatientDataDefinition extends BaseDataDefinition implements PatientDataDefinition {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required = true)
	private Map<String, Mapped<PatientDataDefinition>> containedDataDefinitions;
	
	@ConfigurationProperty(required = true)
	private ScriptingLanguage scriptType;
	
	@ConfigurationProperty(required = true)
	private String scriptCode;
	
	@Override
	public Class<?> getDataType() {
		return Object.class;
	}
	
	/**
	 * Gets the contained patients data definitions
	 * 
	 * @return the containedDataDefinitions
	 */
	public Map<String, Mapped<PatientDataDefinition>> getContainedDataDefinitions() {
		if (containedDataDefinitions == null) {
			containedDataDefinitions = new HashMap<String, Mapped<PatientDataDefinition>>();
		}
		return containedDataDefinitions;
	}
	
	/**
	 * Sets the contained patients data definitions
	 * 
	 * @param containedDataDefinitions the containedDataDefinitions to set
	 */
	
	public void setContainedDataDefinitions(Map<String, Mapped<PatientDataDefinition>> containedDataDefinitions) {
		this.containedDataDefinitions = containedDataDefinitions;
	}
	
	/**
	 * Adds contained patients data definitions
	 * 
	 * @param key
	 * @param patientDataDefinitions the patient data definitions
	 */
	public void addContainedDataDefinition(String key, Mapped<PatientDataDefinition> patientDataDefinitions) {
		getContainedDataDefinitions().put(key, patientDataDefinitions);
	}
	
	/**
	 * Adds a contained patient data definition
	 * 
	 * @param key
	 * @param patientDataDefintion the patient data definition
	 * @param mappings
	 */
	public void addContainedDataDefinition(String key, PatientDataDefinition patientDataDefintion,
	                                       Map<String, Object> mappings) {
		addContainedDataDefinition(key, new Mapped<PatientDataDefinition>(patientDataDefintion, mappings));
	}
	
	public ScriptingLanguage getScriptType() {
		return scriptType;
	}
	
	public void setScriptType(ScriptingLanguage scriptType) {
		this.scriptType = scriptType;
	}
	
	public String getScriptCode() {
		return scriptCode;
	}
	
	public void setScriptCode(String scriptCode) {
		this.scriptCode = scriptCode;
	}
}
