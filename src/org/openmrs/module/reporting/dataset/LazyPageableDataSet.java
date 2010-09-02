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
package org.openmrs.module.reporting.dataset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PageableDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.LazyPageableDataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * This is a {@link DataSet} that allows you to request the rows for a subset of the
 * input cohort, and it will generate those rows on demand (by delegating to a
 * {@link LazyPageableDataSetEvaluator}).
 */
public class LazyPageableDataSet implements PageableDataSet {
	
	LazyPageableDataSetEvaluator evaluator;
	EvaluationContext context;
	PageableDataSetDefinition definition;
	
	// A snapshot of patient ids so we can easily page through the patients on demand
	List<Integer> cohortPatientIds;
	// A snapshot of the columns (these won't change)
	DataSetMetaData dataSetMetadata;
	
	/**
	 * @param evaluator
	 * @param evalContext
	 * @param definition
	 */
	public LazyPageableDataSet(LazyPageableDataSetEvaluator evaluator, EvaluationContext evalContext,
	                           PageableDataSetDefinition definition) {
		this.evaluator = evaluator;
		this.context = evalContext;
		this.definition = definition;
		
		if (evalContext.getBaseCohort() != null) {
			cohortPatientIds = new ArrayList<Integer>(evalContext.getBaseCohort().getMemberIds());
		} else {
			cohortPatientIds = new ArrayList<Integer>(Context.getPatientSetService().getAllPatients().getMemberIds());
		}
		
		dataSetMetadata = definition.getDataSetMetadata();
	}
	
	/**
	 * @see PageableDataSet#rowsForCohortSubset(int, int)
	 */
	public List<DataSetRow> rowsForCohortSubset(int start, int size) {
		// TODO add a maximum number of patients to be evaluated at a time, which will keep memory consumption down
		int end = start + size;
		if (size < 0 || end > cohortPatientIds.size())
			end = cohortPatientIds.size();
		List<DataSetRow> list = evaluator.evaluatePartial(definition, context, cohortPatientIds.subList(start, end));
		return list;
	}
	
	/**
	 * @see DataSet#iterator()
	 */
	public Iterator<DataSetRow> iterator() {
		return rowsForCohortSubset(0, -1).iterator();
	}

	
	/**
	 * @see DataSet#getMetaData()
	 */
	public DataSetMetaData getMetaData() {
		return dataSetMetadata;
	}
	
	/**
	 * @see DataSet#getContext()
	 */
	public EvaluationContext getContext() {
		return context;
	}
	
	/**
	 * @see DataSet#getDefinition()
	 */
	public DataSetDefinition getDefinition() {
		return definition;
	}
	
	public int getCohortSize() {
		return cohortPatientIds.size();
	}
	
}
