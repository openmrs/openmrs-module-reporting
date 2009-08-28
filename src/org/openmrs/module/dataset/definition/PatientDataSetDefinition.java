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
package org.openmrs.module.dataset.definition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.ProgramWorkflow;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;
import org.openmrs.module.dataset.definition.evaluator.PatientDataSetEvaluator;

/**
 * Definition of a dataset that produces one-row-per-patient table. 
 * @see PatientDataSetEvaluator
 */
public class PatientDataSetDefinition extends BaseDataSetDefinition {

	// Serial version UID
	private static final long serialVersionUID = 6405583324151111487L;
	
    // ***** FIXED COLUMNS *****
	public static DataSetColumn PATIENT_ID = new SimpleDataSetColumn("patientId", Integer.class);
	public static DataSetColumn FAMILY_NAME = new SimpleDataSetColumn("family_name", String.class);
	public static DataSetColumn GIVEN_NAME = new SimpleDataSetColumn("given_name", String.class);
	public static DataSetColumn AGE = new SimpleDataSetColumn("age", Integer.class);
	public static DataSetColumn GENDER = new SimpleDataSetColumn("gender", String.class);
	
	//***** PROPERTIES ******
	public List<PersonAttributeType> personAttributeTypes;
	public List<PatientIdentifierType> identifierTypes;
	public List<ProgramWorkflow> programWorkflows;
	
	/**
	 * Constructor
	 */
	public PatientDataSetDefinition() {
		super();
	}
	
	/**
	 * Public constructor
	 * 
	 * @param name
	 * @param description
	 * @param questions
	 */
	public PatientDataSetDefinition(String name, String description) {
		this();
		this.setName(name);
		this.setDescription(description);
	}
	
	//****** INSTANCE METHODS ******
	
	/** 
	 * @see DataSetDefinition#getColumns()
	 */
	public List<DataSetColumn> getColumns() {
		List<DataSetColumn> cols = Arrays.asList(PATIENT_ID, FAMILY_NAME, GIVEN_NAME, AGE, GENDER);
		for (PatientIdentifierType t : getIdentifierTypes()) {
			cols.add(new SimpleDataSetColumn(t.getName(), t.getName(), String.class));
		}
		for (PersonAttributeType t : getPersonAttributeTypes()) {
			cols.add(new SimpleDataSetColumn(t.getName(), t.getName(), String.class));
		}
		for (ProgramWorkflow t : getProgramWorkflows()) {
			cols.add(new SimpleDataSetColumn(t.getName(), t.getName(), String.class));
		}
		return cols;
	}
	
	//****** PROPERTY ACCESS ******

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
	public void setPersonAttributeTypes(
			List<PersonAttributeType> personAttributeTypes) {
		this.personAttributeTypes = personAttributeTypes;
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
}
