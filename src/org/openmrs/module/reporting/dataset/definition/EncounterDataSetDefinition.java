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
import java.util.List;

import org.openmrs.module.reporting.dataset.column.DataSetColumn;
import org.openmrs.module.reporting.dataset.column.SimpleDataSetColumn;
import org.openmrs.module.reporting.dataset.definition.evaluator.EncounterDataSetEvaluator;

/**
 * Definition of a dataset that produces one-row-per-encounter table.
 * @see EncounterDataSetEvaluator
 */
public class EncounterDataSetDefinition extends BaseDataSetDefinition {
	
    // ***** FIXED COLUMNS *****
	public static DataSetColumn PATIENT_ID = new SimpleDataSetColumn("patientId", Integer.class);
	public static DataSetColumn ENCOUNTER_ID = new SimpleDataSetColumn("encounter_id", Integer.class);
	public static DataSetColumn ENCOUNTER_TYPE = new SimpleDataSetColumn("encounter_type", String.class);
	public static DataSetColumn FORM = new SimpleDataSetColumn("form", String.class);
	public static DataSetColumn LOCATION = new SimpleDataSetColumn("location", String.class);
	
	/**
	 * Constructor
	 */
	public EncounterDataSetDefinition() {
		super();
	}
	
	/**
	 * Full constructor
	 */
	public EncounterDataSetDefinition(String name, String description) { 
		super(name, description);	
	}
	
	/**
     * @see DataSetDefinition#getColumns()
	 */
    public List<DataSetColumn> getColumns() {
    	return Arrays.asList(PATIENT_ID, ENCOUNTER_ID, ENCOUNTER_TYPE, FORM, LOCATION);
	}
}
