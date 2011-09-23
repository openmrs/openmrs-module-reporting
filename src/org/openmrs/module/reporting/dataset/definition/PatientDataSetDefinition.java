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

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataSetDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * DataSetDefinition for Producing a DataSet that has one row per Patient
 * @see DataSetDefinition
 */
public class PatientDataSetDefinition extends RowPerObjectDataSetDefinition {
	
    public static final long serialVersionUID = 1L;
    
    //***** PROPERTIES *****
    
    @ConfigurationProperty
    private List<Mapped<? extends CohortDefinition>> rowFilters;
 
    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public PatientDataSetDefinition() {
    	super();
    }

	/**
	 * Public constructor
	 */
	public PatientDataSetDefinition(String name) { 
		super(name);
	}
	
	//***** INSTANCE METHODS *****
		
	/**
	 * Adds a new Column Definition given the passed parameters
	 */
	public void addColumn(String name, DataDefinition dataDefinition, String mappings, DataConverter converter) {
		if (dataDefinition instanceof PatientDataDefinition) {
			getColumnDefinitions().add(new RowPerObjectColumnDefinition(name, dataDefinition, mappings, converter));
		}
		else if (dataDefinition instanceof PersonDataDefinition) {
			PatientDataDefinition pdd = new PersonToPatientDataDefinition((PersonDataDefinition) dataDefinition);
			getColumnDefinitions().add(new RowPerObjectColumnDefinition(name, pdd, mappings, converter));
		}
		else {
			throw new IllegalArgumentException("Unable to add data definition of type " + dataDefinition.getClass().getSimpleName());
		}
	}
	
	/**
	 * Adds a the Column Definitions defined in the passed DataSetDefinition
	 */
	public void addColumns(String name, RowPerObjectDataSetDefinition dataSetDefinition, String mappings, DataConverter converter,
						   TimeQualifier whichValues, Integer numberOfValues) {
		
		// Ensure that the DSD being passed in supports a Patient ID column.  If so, we can join against it
		try {
			RowPerObjectDataSetDefinition def = dataSetDefinition.getClass().newInstance();
			def.addColumn("Patient ID", new PatientIdDataDefinition(), null, null);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Unable to add columns from " + dataSetDefinition.getClass().getSimpleName(), e);
		}
		
		addColumn(name, new PatientDataSetDataDefinition(dataSetDefinition, whichValues, numberOfValues), mappings, converter);
	}
	
    //***** PROPERTY ACCESS *****
	
	/**
	 * @return the rowFilters
	 */
	public List<Mapped<? extends CohortDefinition>> getRowFilters() {
		if (rowFilters == null) {
			rowFilters = new ArrayList<Mapped<? extends CohortDefinition>>();
		}
		return rowFilters;
	}

	/**
	 * @param rowFilters the rowFilters to set
	 */
	public void setRowFilters(List<Mapped<? extends CohortDefinition>> rowFilters) {
		this.rowFilters = rowFilters;
	}
}