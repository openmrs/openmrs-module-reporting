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
package org.openmrs.module.dataset;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.CohortDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;

/**
 * A dataset which represents a list of cohorts, each of which has a name. For example a
 * CohortDataset might represent: "1. Total # of Patients" -> 123 "1.a. Male Adults" -> Cohort of 54
 * patients "1.b. Female Adults" -> Cohort of 43 patients "1.c. Male Children" -> Cohort of 12
 * patients "1.d. Female Children" -> Cohort of 14 patient ...
 * 
 * @see CohortDataSetDefinition
 * @see CohortDataSetProvider
 */
public class CohortDataSet extends MapDataSet<Cohort> {
	
	
	private String name;

	private CohortDataSetDefinition dataSetDefinition;
	
	private EvaluationContext evaluationContext;
		
	private Map<DataSetColumn, Cohort> cohortData;
	
	public CohortDataSet() {
		cohortData = new LinkedHashMap<DataSetColumn, Cohort>();
	}

	public CohortDataSet(CohortDataSetDefinition cohortDataSetDefinition) { 
		this.name = cohortDataSetDefinition.getName();
		this.dataSetDefinition = cohortDataSetDefinition;
		//this.evaluationContext = evalContext;		
	}
	
	/**
	 * @see org.openmrs.report.MapDataSet#getData()
	 */
	public Map<DataSetColumn, Cohort> getData() {
		return cohortData;
	}
	
	/**
	 * Returns this map as a single-row data set
	 * 
	 * @see org.openmrs.report.DataSet#iterator()
	 */
	public Iterator<Map<DataSetColumn, Cohort>> iterator() {
		return Collections.singleton(cohortData).iterator();
	}
	
	public Map<DataSetColumn, Cohort> getCohortData() {
		return cohortData;
	}
	
	public void setCohortData(Map<DataSetColumn, Cohort> cohortData) {
		this.cohortData = cohortData;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @see DataSet#getDefinition()
	 */
	public DataSetDefinition getDataSetDefinition() {
		return dataSetDefinition;
	}
	
	/**
	 * @param definition the definition to set
	 */
	public void setDataSetDefinition(CohortDataSetDefinition definition) {
		this.dataSetDefinition = definition;
	}
	
	/**
	 * @see DataSet#getEvaluationContext()
	 */
	public EvaluationContext getEvaluationContext() {
		return evaluationContext;
	}
	
	/**
	 * @param evaluationContext the evaluationContext to set
	 */
	public void setEvaluationContext(EvaluationContext evaluationContext) {
		this.evaluationContext = evaluationContext;
	}
	
}
