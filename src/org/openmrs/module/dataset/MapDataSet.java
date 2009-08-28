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

import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;

/**
 * DataSet which is key-value pairs, instead of a full two-dimensional table
 */
public class MapDataSet<T> implements DataSet<T> {
	
	//****** PROPERTIES ******
	
	private String name;
	private DataSetDefinition definition;
	private EvaluationContext context;
	private DataSetRow<T> data = new DataSetRow<T>();
	
	/**
	 * Default Constructor
	 */
	public MapDataSet() { }
	
	/**
	 * Returns this map as a single-row data set
	 * @see DataSet#iterator()
	 */
	public Iterator<DataSetRow<T>> iterator() {
		return Collections.singleton(data).iterator();
	}

	/**
	 * Convenience method for JSTL method.  
	 * TODO This will be removed once we get a decent solution for the dataset iterator solution.  
	 */
	public Iterator<DataSetRow<T>> getIterator() {
		return iterator();
	}
	
	/**
     * Adds a Data Element to this DataSet
     * @param key - The column key to add this element to
     * @param dataElement - The data to add
     */
    public void addData(DataSetColumn column, T dataElement) {
    	data.addColumnValue(column, dataElement);
    }
    
	/**
     * Adds a Data Element to this DataSet
     * @param key - The column key to add this element to
     * @param dataElement - The data to add
     */
    public void addData(String columnKey, T dataElement) {
    	DataSetColumn c = definition.getColumn(columnKey);
    	if (c != null) {
    		data.addColumnValue(c, dataElement);
    	}
    	else {
    		throw new IllegalArgumentException("Column with key <" + columnKey + "> is not valid.");
    	}
    }

	/** 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		if (data != null) {
			return data.toString();
		}
		return super.toString();
	}

	/**
     * @return the name
     */
    public String getName() {
    	return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
    	this.name = name;
    }

    /**
	 * @return the definition
	 */
	public DataSetDefinition getDefinition() {
		return definition;
	}

	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(DataSetDefinition definition) {
		this.definition = definition;
	}

	/**
	 * @return the context
	 */
	public EvaluationContext getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(EvaluationContext context) {
		this.context = context;
	}

	/**
     * @return the data
     */
    public DataSetRow<T> getData() {
    	return data;
    }
    
    /**
     * @param data the data to set
     */
    public void setData(DataSetRow<T> data) {
    	this.data = data;
    }	
}
