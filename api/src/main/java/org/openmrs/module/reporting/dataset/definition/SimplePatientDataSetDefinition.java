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

import java.util.ArrayList;
import java.util.List;

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.ProgramWorkflow;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * This is a simple example of how one might implement a row-per-Patient DataSetDefinition
 * There are no guarantees that this class will be backwards compatible, or exist in a future
 * release, so should be used with caution
 * @see SimplePatientDataSetEvaluator
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.SimplePatientDataSetDefinition")
public class SimplePatientDataSetDefinition extends BaseDataSetDefinition {

	public static final long serialVersionUID = 6405583324151111487L;
	
	//**** PROPERTIES *****
	
	@ConfigurationProperty(group="properties")
	private List<String> patientProperties;
	
	@ConfigurationProperty(group="properties")
	private List<PersonAttributeType> personAttributeTypes;
	
	@ConfigurationProperty(group="properties")
	private List<PatientIdentifierType> identifierTypes;
	
	@ConfigurationProperty(group="properties")
	private List<ProgramWorkflow> programWorkflows;
	
	/**
	 * Constructor
	 */
	public SimplePatientDataSetDefinition() {
		super();
	}
	
	/**
	 * Public constructor with name and description
	 */
	public SimplePatientDataSetDefinition(String name, String description) {
		super(name, description);
	}
	
	//****** PROPERTY ACCESS ******

	/**
	 * @return the patientProperties
	 */
	public List<String> getPatientProperties() {
		if (patientProperties == null) {
			patientProperties = new ArrayList<String>();
		}
		return patientProperties;
	}

	/**
	 * @param patientProperties the patientProperties to set
	 */
	public void setPatientProperties(List<String> patientProperties) {
		this.patientProperties = patientProperties;
	}
	
	/**
	 * @param property the property to add
	 */
	public void addPatientProperty(String property) {
		getPatientProperties().add(property);
	}

	/**
	 * @return the personAttributeTypes
	 */
	public List<PersonAttributeType> getPersonAttributeTypes() {
		if (personAttributeTypes == null) {
			personAttributeTypes = new ArrayList<PersonAttributeType>();
		}
		return personAttributeTypes;
	}

	/**
	 * @param personAttributeTypes the personAttributeTypes to set
	 */
	public void setPersonAttributeTypes(List<PersonAttributeType> personAttributeTypes) {
		this.personAttributeTypes = personAttributeTypes;
	}
	
	/**
	 * 
	 * @param personAttributeType to add
	 */
	public void addPersonAttributeType(PersonAttributeType personAttributeType) {
		getPersonAttributeTypes().add(personAttributeType);
	}

	/**
	 * @return the identifierTypes
	 */
	public List<PatientIdentifierType> getIdentifierTypes() {
		if (identifierTypes == null) {
			identifierTypes = new ArrayList<PatientIdentifierType>();
		}
		return identifierTypes;
	}

	/**
	 * @param identifierTypes the identifierTypes to set
	 */
	public void setIdentifierTypes(List<PatientIdentifierType> identifierTypes) {
		this.identifierTypes = identifierTypes;
	}
	
	/**
	 * @param identifierType to add
	 */
	public void addIdentifierType(PatientIdentifierType identifierType) {
		getIdentifierTypes().add(identifierType);
	}

	/**
	 * @return the programWorkflows
	 */
	public List<ProgramWorkflow> getProgramWorkflows() {
		if (programWorkflows == null) {
			programWorkflows = new ArrayList<ProgramWorkflow>();
		}
		return programWorkflows;
	}

	/**
	 * @param programWorkflows the programWorkflows to set
	 */
	public void setProgramWorkflows(List<ProgramWorkflow> programWorkflows) {
		this.programWorkflows = programWorkflows;
	}
	
	/**
	 * @param programWorkflow to add
	 */
	public void addProgramWorkflow(ProgramWorkflow programWorkflow) {
		getProgramWorkflows().add(programWorkflow);
	}
}
