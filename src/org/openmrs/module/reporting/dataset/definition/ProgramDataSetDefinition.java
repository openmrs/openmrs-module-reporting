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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.openmrs.Program;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.column.DataSetColumn;
import org.openmrs.module.reporting.dataset.column.SimpleDataSetColumn;
import org.openmrs.module.reporting.dataset.definition.evaluator.ProgramDataSetEvaluator;

/**
 * Definition of a dataset that produces one-row-per-PatientProgram. Output might look like:
 * patientId, programName, programId, enrollmentDate, completionDate, patientProgramId
 * 123, "HIV PROGRAM", 1, "2008-01-01", null, 5383<br/>
 * 123, "TB PROGRAM", 2, "2006-04-11", "2006-10-11", 4253
 * 
 * @see ProgramDataSetEvaluator
 */
public class ProgramDataSetDefinition extends BaseDataSetDefinition {
	
    private static final long serialVersionUID = -1408727201579935500L;
    
    // ***** FIXED COLUMNS *****
	public static DataSetColumn PATIENT_ID = new SimpleDataSetColumn("patientId", Integer.class);
	public static DataSetColumn PROGRAM_NAME = new SimpleDataSetColumn("programName", String.class);
	public static DataSetColumn PROGRAM_ID = new SimpleDataSetColumn("programId", Integer.class);
	public static DataSetColumn ENROLLMENT_DATE = new SimpleDataSetColumn("enrollmentDate", Date.class);
	public static DataSetColumn COMPLETION_DATE = new SimpleDataSetColumn("completionDate", Date.class);
	public static DataSetColumn PATIENT_PROGRAM_ID = new SimpleDataSetColumn("patientProgramId", Integer.class);

    // ***** PROPERTIES *****
    
	private Collection<Program> programs;
	private CohortDefinition cohortDefinition;

	/**
	 * Default constructor
	 */
	public ProgramDataSetDefinition() {
		super();
	}

	/**
	 * Full constructor
	 */
	public ProgramDataSetDefinition(String name, String description, Collection<Program> programs, CohortDefinition cohortDefinition) {
		this();
		this.setName(name);
		this.setDescription(description);
		this.programs = programs;
		this.cohortDefinition = cohortDefinition;
	}
	
	//****** INSTANCE METHODS ******
	
	/** 
	 * @see DataSetDefinition#getColumns()
	 */
	public List<DataSetColumn> getColumns() {
		return Arrays.asList(PATIENT_ID, PROGRAM_NAME, PROGRAM_ID, ENROLLMENT_DATE, COMPLETION_DATE, PATIENT_PROGRAM_ID);
	}
	
	//****** PROPERTY ACCESS ********

	/**
	 * @return the programs
	 */
	public Collection<Program> getPrograms() {
		if (programs == null) {
			programs = new HashSet<Program>();
		}
		return programs;
	}

	/**
	 * @param programs the programs to set
	 */
	public void setPrograms(Collection<Program> programs) {
		this.programs = programs;
	}

	/**
	 * @return the cohortDefinition
	 */
	public CohortDefinition getCohortDefinition() {
		return cohortDefinition;
	}

	/**
	 * @param cohortDefinition the cohortDefinition to set
	 */
	public void setCohortDefinition(CohortDefinition cohortDefinition) {
		this.cohortDefinition = cohortDefinition;
	}
}
