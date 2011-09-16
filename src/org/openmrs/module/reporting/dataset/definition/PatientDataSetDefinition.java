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
import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * DataSetDefinition for Producing a DataSet that has one row per Patient
 * @see DataSetDefinition
 */
public class PatientDataSetDefinition extends BaseDataSetDefinition {
	
    public static final long serialVersionUID = 1L;
    
    //***** PROPERTIES *****
    
    @ConfigurationProperty
    private List<Mapped<? extends CohortDefinition>> rowFilters;
    
    @ConfigurationProperty
    private List<ColumnDefinition> columnDefinitions;
 
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

	/**
	 * @return the columnDefinitions
	 */
	public List<ColumnDefinition> getColumnDefinitions() {
		if (columnDefinitions == null) {
			columnDefinitions = new ArrayList<ColumnDefinition>();
		}
		return columnDefinitions;
	}

	/**
	 * @param columnDefinitions the columnDefinitions to set
	 */
	public void setColumnDefinitions(List<ColumnDefinition> columnDefinitions) {
		this.columnDefinitions = columnDefinitions;
	}
}