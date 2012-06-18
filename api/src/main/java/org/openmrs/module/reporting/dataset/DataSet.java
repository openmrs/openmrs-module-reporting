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
