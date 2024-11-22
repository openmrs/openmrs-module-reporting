/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
