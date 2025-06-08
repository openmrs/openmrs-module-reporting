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

import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.Evaluated;

/**
 * Implementations of this interface are the result you get of evaluating a {@link DataSetDefinition}
 * with a {@link DataSetEvaluator}.
 * A simple data set allows you to get an iterator over its rows, and to get metadata about its columns. 
 * @see DataSetDefinition
 * @see DataSetEvaluator
 */
public interface DataSet extends Evaluated<DataSetDefinition>, Iterable<DataSetRow> {
	
	/**
	 * Each iteration of this iterator returns a DataSetRow
	 * @return an iterator over the rows in this dataset.
	 * @see Iterable#iterator()
	 */
	public Iterator<DataSetRow> iterator();
	
	/**
	 * @return the {@link DataSetMetaData} which contains information about the columns in this DataSet
	 */
	public DataSetMetaData getMetaData();
}
