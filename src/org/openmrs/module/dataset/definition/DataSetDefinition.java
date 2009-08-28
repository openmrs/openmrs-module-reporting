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

import java.util.List;

import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.evaluation.Definition;

/**
 * Implementations of this interface describe the metadata that can be evaluated to produce a {@link DataSet}. 
 * This is one of three interfaces that work together to define and evaluate an OpenMRS DataSet. 
 * An implementation of {@link DataSetEvaluator} transforms one or more implementations of {@link DataSetDefinition}
 * to produce a specific type of {@link DataSet}.
 * @see DataSetEvaluator
 * @see DataSet
 */
public interface DataSetDefinition extends Definition {
        
	/**
	 * Gets a list of dataset columns.
	 * @return
	 */
	public List<DataSetColumn> getColumns();
	
	/**
	 * Returns the {@link DataSetColumn} defined by the passed column key
	 * @param columnKey
	 * @return
	 */
	public DataSetColumn getColumn(String columnKey);
	
	/**
	 * Adds a {@link DataSetColumn}
	 * @param column
	 */
	public void addColumn(DataSetColumn column);
}	
