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

import java.util.Collections;
import java.util.Iterator;

import org.openmrs.module.reporting.dataset.column.DataSetColumn;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * DataSet which is key-value pairs, instead of a full two-dimensional table
 */
public class MapDataSet implements DataSet {
	
	//****** PROPERTIES ******
	
	private String name;
	private DataSetDefinition definition;
	private EvaluationContext context;
	private DataSetRow data = new DataSetRow();
	
    /**
     * Default Constructor which creates an empty DataSet for the given definition and evaluationContext
     * @param definition
     * @param evaluationContext
     */
    public MapDataSet(DataSetDefinition definition, EvaluationContext evaluationContext) {
        this.definition = definition;
        this.context = evaluationContext;
    }
	
	/**
	 * Returns this map as a single-row data set
	 * @see DataSet#iterator()
	 */
	public Iterator<DataSetRow> iterator() {
		return Collections.singleton(data).iterator();
	}
	
	/**
     * Adds a Data Element to this DataSet
     * @param key - The column key to add this element to
     * @param dataElement - The data to add
     */
    public void addData(DataSetColumn column, Object dataElement) {
    	data.addColumnValue(column, dataElement);
    }
    
	/**
     * Adds a Data Element to this DataSet
     * @param columnName - The column name to add this element to
     * @param dataElement - The data to add
     */
    public void addData(String columnName, Object dataElement) {
    	DataSetColumn c = definition.getColumn(columnName);
    	if (c != null) {
    		data.addColumnValue(c, dataElement);
    	}
    	else {
    		throw new IllegalArgumentException("Column with name <" + columnName + "> is not valid.");
    	}
    }

	/**
     * Gets a Data Element from this DataSet
     * @param key - The column key to add this element to
     * @param dataElement - The data to add
     */
    public Object getData(DataSetColumn column) {
    	return data.getColumnValue(column);
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
    public DataSetRow getData() {
    	return data;
    }
    
    /**
     * @param data the data to set
     */
    public void setData(DataSetRow data) {
    	this.data = data;
    }	
}
