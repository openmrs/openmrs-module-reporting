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

import org.openmrs.OpenmrsMetadata;
import org.openmrs.module.evaluation.parameter.Parameterizable;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.DataSet;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Implementations of this interface describe the metadata that can be evaluated to produce a
 * {@link DataSet}. This is one of three interfaces that work together to define and evaluate an
 * OpenMRS DataSet. You need to implement all three of DataSetProvider, {@link DataSetDefinition},
 * and {@link DataSet} in order to get useful behavior. For example:
 * {@link ObsDataSetProvider}, {@link ObsDataSetDefinition}, and
 * {@link ObsDataSet} The metadata that describes what data will be produced is defined in
 * this interface The logic that evaluates that metadata goes in an implementation of
 * {@link DataSetProvider}. After evaluation, the data is represented by a {@link DataSet}.
 * 
 * @see DataSetProvider
 * @see DataSet
 */
@Root(strict = false)
public interface DataSetDefinition extends OpenmrsMetadata, Parameterizable {
		
	/**
	 * @return	the number of columns 
	 */
	public Integer getColumnCount();
	
	/**
	 * Gets a list of dataset columns.
	 * 
	 * @return
	 */
	public List<DataSetColumn> getColumns();
	
	/**
	 * Gets a list of column keys.
	 * 
	 * @return <code>List<String></code> of the column keys
	 */
	@Deprecated
	public List<String> getColumnKeys();
	
	/**
	 * Gets a list of the datatype of the columns
	 * 
	 * @return <code>List<Class></code> of the column datatypes
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public List<Class> getColumnDatatypes();
	
}
