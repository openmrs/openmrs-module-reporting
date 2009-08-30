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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.column.SimpleDataSetColumn;
import org.openmrs.module.dataset.definition.evaluator.CohortDataSetEvaluator;
import org.openmrs.module.evaluation.parameter.Mapped;

/**
 * Metadata that defines a MapDataSet<Cohort>. 
 * (I.e. a list of cohorts, each of which has a name)<p>
 * For example a CohortDatasetDefinition might represent:<br/>
 * "1. Total # of Patients" -> (CohortDefinition) everyone <br/>
 * "1.a. Male Adults" -> (CohortDefinition) Male AND Adult<br/>
 * "1.b. Female Adults" -> (CohortDefinition) Female AND Adult<br/>
 * "1.c. Male Children" -> (CohortDefinition) Male AND NOT Adult<br/>
 * "1.d. Female Children" -> (CohortDefinition) Female AND NOT Adult ...
 * 
 * @see MapDataSet<Cohort>
 * @see CohortDataSetEvaluator
 */
public class CohortDataSetDefinition extends BaseDataSetDefinition {
	
	//***** PROPERTIES *****
	private Map<DataSetColumn, Mapped<? extends CohortDefinition>> definitions;
	
	/**
	 * Default constructor
	 */
	public CohortDataSetDefinition() {
		super();
	}
	
	/**
	 * Public constructor
	 * @param name
	 * @param description
	 */
	public CohortDataSetDefinition(String name, String description) { 
		super(name, description);
	}
	
	//****** INSTANCE METHODS ******
	
	/** 
	 * @see DataSetDefinition#getColumns()
	 */
	public List<DataSetColumn> getColumns() {
		return new ArrayList<DataSetColumn>(definitions.keySet());
	}

	
	/**
	 * Add the given CohortDefinition with the given key, display name, and mappings
	 * @param name
	 * @param displayName
	 * @param cohortDefinition
	 */
	public void addDefinition(String key, String displayName, CohortDefinition definition, Map<String, Object> mappings) {
		addDefinition(key, displayName, new Mapped<CohortDefinition>(definition, mappings));
	}
	
	/**
	 * Add the given Mapped<CohortDefinition> with the given key and the given display name
	 * @param name
	 * @param displayName
	 * @param cohortDefinition
	 */
	public void addDefinition(String key, String displayName, Mapped<? extends CohortDefinition> definition) {
		getDefinitions().put(new SimpleDataSetColumn(key, displayName, Cohort.class), definition);
	}
	
	//****** PROPERTY ACCESS ******

	/**
	 * @return the definitions
	 */
	public Map<DataSetColumn, Mapped<? extends CohortDefinition>> getDefinitions() {
		if (definitions == null) {
			definitions = new LinkedHashMap<DataSetColumn, Mapped<? extends CohortDefinition>>();
		}
		return definitions;
	}

	/**
	 * @param definitions the definitions to set
	 */
	public void setDefinitions(Map<DataSetColumn, Mapped<? extends CohortDefinition>> definitions) {
		this.definitions = definitions;
	}
}
