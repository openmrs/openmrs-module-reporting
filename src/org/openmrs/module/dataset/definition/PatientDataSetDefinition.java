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
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;

/**
 * Definition of a dataset that produces one-row-per-encounter table. 
 *
 * @see EncounterDataSet
 */
public class PatientDataSetDefinition extends BaseDataSetDefinition {

	// Serial version UID
	private static final long serialVersionUID = 6405583324151111487L;

	// Constants 
	public static final String PATIENT_ID = "patient_id";
	public static final String IMB_ID = "imb_id";
	public static final String NAME = "name";
	public static final String AGE = "age";
	public static final String GENDER = "gender";
	public static final String HEALTH_CENTER = "health_center";
	public static final String TREATMENT_GROUP = "treatment_group";
	
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
		this.setName(name);
		this.setDescription(description);
	}			
	
		
	private static String [] columnKeys = { 		
		PATIENT_ID, IMB_ID, NAME, AGE, GENDER, HEALTH_CENTER, TREATMENT_GROUP
	};
		
	private static Class [] columnDatatypes = { 
		Integer.class, String.class, String.class, Integer.class, String.class, String.class, String.class
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
	
}
