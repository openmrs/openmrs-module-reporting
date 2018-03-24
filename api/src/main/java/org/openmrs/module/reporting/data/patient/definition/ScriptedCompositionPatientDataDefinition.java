/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
