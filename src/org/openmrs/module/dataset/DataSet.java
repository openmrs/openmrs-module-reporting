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

import java.util.Iterator;
import java.util.Map;

import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.ObsDataSetDefinition;
import org.openmrs.module.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.dataset.definition.evaluator.ObsDataSetEvaluator;
import org.openmrs.module.evaluation.Evaluated;

/**
 * Implementations of this interface describe the metadata that can be evaluated to produce a {@link DataSet}. 
 * This is one of three interfaces that work together to define and evaluate an OpenMRS DataSet. 
 * You need to implement all three of {@link DataSetEvaluator}, {@link DataSetDefinition}, and {@link DataSet} 
 * in order to get useful behavior. 
 * For example: {@link ObsDataSetEvaluator}, {@link ObsDataSetDefinition}, and {@link ObsDataSet}.
 * @see DataSetEvaluator
 * @see DataSetDefinition
 */
public interface DataSet<T extends Object> extends Evaluated<DataSetDefinition>, Iterable<Map<DataSetColumn, T>> {
	
	/**
	 * Each iteration of this iterator returns a Map<DataSetColumn, T>.
	 * @return an iterator over the rows in this dataset.
	 * @see Iterable#iterator()
	 */
	public Iterator<Map<DataSetColumn, T>> iterator();
	
	
	public Iterator<Map<DataSetColumn, T>> getIterator();
	
}
