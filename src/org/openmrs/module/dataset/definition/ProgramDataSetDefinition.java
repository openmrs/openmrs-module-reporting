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
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.openmrs.Program;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;

/**
 * Definition of a dataset that produces one-row-per-PatientProgram. Output might look like:
 * patientId, programName, programId, enrollmentDate, completionDate, patientProgramId
 * 123, "HIV PROGRAM", 1, "2008-01-01", null, 5383<br/>
 * 123, "TB PROGRAM", 2, "2006-04-11", "2006-10-11", 4253
 * 
 * @see RowPerProgramEnrollmentDataSet
 */
public class ProgramDataSetDefinition extends BaseDataSetDefinition {
	
    private static final long serialVersionUID = -1408727201579935500L;

	private Collection<Program> programs;
	
	private CohortDefinition filter;

	/**
	 * Default constructor
	 */
	public ProgramDataSetDefinition() {
		programs = new HashSet<Program>();
	}

	/**
	 * Full-arg constructor
	 */
	public ProgramDataSetDefinition(String name, String description, Collection<Program> programs, CohortDefinition filter) {
		this.setName(name);
		this.setDescription(description);
		this.programs = programs;
		this.filter = filter;
		
	}
	
	
	
	public List<Class> getColumnDatatypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getColumnKeys() {
		// TODO Auto-generated method stub
		return null;
	}		
	
	/**
	 * @see org.openmrs.module.datasetDefinition#getColumns()
	 */
	public List<DataSetColumn> getColumns() {
		List<DataSetColumn> ret = new ArrayList<DataSetColumn>();
		ret.add(new SimpleDataSetColumn("patientId", Integer.class));
		ret.add(new SimpleDataSetColumn("programName", String.class));
		ret.add(new SimpleDataSetColumn("programId", Integer.class));
		ret.add(new SimpleDataSetColumn("enrollmentDate", Date.class));
		ret.add(new SimpleDataSetColumn("completionDate", Date.class));
		ret.add(new SimpleDataSetColumn("patientProgramId", Integer.class));
		return ret;
	}
	
	/**
	 * @see org.openmrs.module.evaluation.parameter.Parameterizable#getParameters()
	 */
	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>();
	}
	
	public Collection<Program> getPrograms() {
		return programs;
	}
	
	public void setPrograms(Collection<Program> programs) {
		this.programs = programs;
	}
	
	public CohortDefinition getFilter() {
		return filter;
	}
	
	public void setFilter(CohortDefinition filter) {
		this.filter = filter;
	}
}
