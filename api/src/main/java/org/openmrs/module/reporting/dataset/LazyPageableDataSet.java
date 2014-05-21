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

import org.openmrs.module.reporting.cohort.Cohorts;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PageableDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.dataset.definition.evaluator.LazyPageableDataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is a {@link DataSet} that allows you to request the rows for a subset of the
 * input cohort, and it will generate those rows on demand (by delegating to a
 * {@link LazyPageableDataSetEvaluator}).
 */
public class LazyPageableDataSet implements PageableDataSet {
	
	Integer maximumPatientsToEvaluate = 1000;
	
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
            cohortPatientIds = new ArrayList<Integer>(Cohorts.allPatients(evalContext).getMemberIds());
		}

		dataSetMetadata = definition.getDataSetMetadata();
	}
	
	
	/**
	 * Sets the maximum numbers of patients that should be evaluated at a time by the underyling
	 * {@link DataSetEvaluator}. Lowering this number should cut memory consumption, but may decrease
	 * performance. 
	 * @param maximumPatientsToEvaluate
	 */
	public void setMaximumPatientsToEvaluate(Integer maximumPatientsToEvaluate) {
		this.maximumPatientsToEvaluate = maximumPatientsToEvaluate;
	}
	
	
	/**
	 * If the number of rows requested is greater than the maximumPatientsToEvaluate setting then
	 * the iterator returned will make multiple calls back to the underlying evaluator over its
	 * lifecycle.
	 * @see PageableDataSet#rowsForCohortSubset(int, int)
	 */
	public Iterator<DataSetRow> rowsForCohortSubset(int start, int size) {
		// TODO add a maximum number of patients to be evaluated at a time, which will keep memory consumption down
		int end = start + size;
		if (size < 0 || end > cohortPatientIds.size())
			end = cohortPatientIds.size();
		return createIterator(start, end);
	}
	
	private Iterator<DataSetRow> createIterator(int start, int end) {
		int MAX = (maximumPatientsToEvaluate == null || maximumPatientsToEvaluate <= 0) ? Integer.MAX_VALUE : maximumPatientsToEvaluate;
		if (end - start <= MAX) {
			return evaluator.evaluatePartial(definition, context, cohortPatientIds.subList(start, end));
		} else {
			return new BufferedIterator(evaluator, cohortPatientIds.subList(start, end), MAX);
		}
	}
	
	/**
	 * @see DataSet#iterator()
	 */
	public Iterator<DataSetRow> iterator() {
		return rowsForCohortSubset(0, -1);
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


	/**
	 * Fetches elements one batch at a time from an underyling {@link LazyPageableDataSetEvaluator}.
	 * This iterator is not thread-safe. (There's no reason for it to be.)
	 */
    public class BufferedIterator implements Iterator<DataSetRow> {
	    
    	private LazyPageableDataSetEvaluator evaluator;
    	private List<Integer> patientIds;
    	private Integer batchSize;
    	private int nextIndex = 0;
    	private Iterator<DataSetRow> currentBatchIterator;
    	
	    public BufferedIterator(LazyPageableDataSetEvaluator evaluator, List<Integer> patientIds, Integer batchSize) {
	        this.evaluator = evaluator;
	        this.batchSize = batchSize;
	        this.patientIds = patientIds;
	        getNextBatch();
        }

		private void getNextBatch() {
			if (nextIndex >= patientIds.size()) {
				currentBatchIterator = null;
			} else {
				int start = nextIndex;
				int end = nextIndex + batchSize;
				if (end > patientIds.size())
					end = patientIds.size();
				List<Integer> batch = patientIds.subList(start, end);
				currentBatchIterator = evaluator.evaluatePartial(definition, context, batch);
				nextIndex = end;
			}
        }

	    public boolean hasNext() {
			if (!currentBatchIterator.hasNext())
				getNextBatch();
			return currentBatchIterator != null;
	    }
	    
	    public DataSetRow next() {
	    	if (!currentBatchIterator.hasNext())
				getNextBatch();
		    return currentBatchIterator.next();
	    }
	    
	    public void remove() {
		    throw new UnsupportedOperationException();
	    }
	    
    }

}
