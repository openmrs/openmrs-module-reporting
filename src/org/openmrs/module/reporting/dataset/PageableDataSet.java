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

import java.util.Iterator;

import org.openmrs.module.reporting.dataset.definition.PageableDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;

/**
 * The result of evaluating a {@link PageableDataSetDefinition} with its
 * {@link DataSetEvaluator}. It allows you get a subset of its rows at a time.
 * Typically this will be faster than getting all the rows, but this is not guaranteed.
 */
public interface PageableDataSet extends DataSet {
	
	/**
	 * Gets the rows in this dataset that correspond to a subset of the patients in
	 * the input cohort. If this is a row-per-patient data set, then this method will
	 * typically return 'size' rows, but for non-row-per-patient data sets, the
	 * number of rows returned by this iterator can be variable.
	 * @param start start from this patient in the input cohort
	 * @param size include this many patients (a negative number means return the rest
	 * of the data set
	 * @return an iterator over the specified subset of the input cohort. If there are
	 * fewer than 'size' patients left from 'start' then fewer rows will be returned. 
	 */
	Iterator<DataSetRow> rowsForCohortSubset(int start, int size);

}
