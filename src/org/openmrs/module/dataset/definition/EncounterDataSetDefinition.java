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
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;

/**
 * Definition of a dataset that produces one-row-per-encounter table. 
 *
 * @see EncounterDataSet
 */
public class EncounterDataSetDefinition extends BaseDataSetDefinition {
	
    private static final long serialVersionUID = -8381653953345505724L;
	
    // Constants 
	public static final String ENCOUNTER_ID		= "encounter_id";
	public static final String ENCOUNTER_TYPE 	= "encounter_type";
	public static final String FORM 			= "form";
	public static final String LOCATION 		= "location";
	public static final String PERSON 			= "person";
	public static final String GENDER 			= "gender";
	public static final String AGE 				= "age";
	
	// Attributes 
	private Date toDate;
	private Date fromDate;
	private CohortDefinition filter;
	private Collection<Concept> questions;	
	
	/**
	 * Constructor
	 */
	public EncounterDataSetDefinition() {
		questions = new HashSet<Concept>();		
	}
		
	private static String [] columnKeys = { 		
		ENCOUNTER_ID, ENCOUNTER_TYPE, FORM, LOCATION, PERSON, GENDER, AGE	
	};
		
	private static Class [] columnDatatypes = { 
		Integer.class, String.class, String.class, String.class, String.class, String.class, Integer.class
	};
		
		
	/**
	 * @see org.openmrs.report.DataSetDefinition#getColumnDatatypes()
     * TODO Move to BaseDataSetDefinition
	 */
	@SuppressWarnings("unchecked")
	public List<Class> getColumnDatatypes() {
		return Arrays.asList(columnDatatypes);
	}
	
	/**
	 * @see org.openmrs.report.DataSetDefinition#getColumnKeys()
     * TODO Move to BaseDataSetDefinition
	 */
	public List<String> getColumnKeys() {
		return Arrays.asList(columnKeys);
	}
	
	/**
     * @see org.openmrs.module.dataset.definition.DataSetDefinition#getColumns()
     * TODO Move to BaseDataSetDefinition
	 */
    public List<DataSetColumn> getColumns() {    	
    	List<DataSetColumn> columns = new ArrayList<DataSetColumn>();
    	for (int i = 0; i < columnKeys.length; i++) {     		
    		DataSetColumn column = 
    			new SimpleDataSetColumn(columnKeys[i], columnDatatypes[i]);
    		columns.add(column);
    	}
    	return columns;

	}
	
	/**
	 * @see org.openmrs.module.evaluation.parameter.Parameterizable#getParameters()
	 */
	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>();
	}
	
	/**
	 * @return the filter
	 */
	public CohortDefinition getFilter() {
		return filter;
	}
	
	/**
	 * @param filter the filter to set
	 */
	public void setFilter(CohortDefinition filter) {
		this.filter = filter;
	}
	
	/**
	 * @return the fromDate
	 */
	public Date getFromDate() {
		return fromDate;
	}
	
	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	/**
	 * @return the toDate
	 */
	public Date getToDate() {
		return toDate;
	}
	
	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
}
