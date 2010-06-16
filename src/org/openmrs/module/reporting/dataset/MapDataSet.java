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

import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * DataSet which is key-value pairs, instead of a full two-dimensional table
 */
public class MapDataSet extends SimpleDataSet {
	
	//****** PROPERTIES ******
	
	private DataSetDefinition definition;
	private EvaluationContext context;
	
	//****** CONSTRUCTORS ******
	
    /**
     * Default Constructor which creates an empty DataSet for the given definition and evaluationContext
     * @param definition
     * @param evaluationContext
     */
    public MapDataSet(DataSetDefinition definition, EvaluationContext evaluationContext) {
    	super(definition, evaluationContext);
    }
    
    //****** INSTANCE METHODS ******
	
	/**
     * Adds a Data Element to this DataSet
     * @param key - The column key to add this element to
     * @param dataElement - The data to add
     */
    public void addData(DataSetColumn column, Object dataElement) {
    	DataSetRow row = null;
    	if (getRows().isEmpty()) {
    		row = new DataSetRow();
    		getRows().add(row);
    	}
    	else {
    		row = getRows().get(0);
    	}
    	row.addColumnValue(column, dataElement);
    	getMetaData().addColumn(column);
    }
    
	/**
     * Gets the DataSetRow for this DataSet
     * @param dataElement - The data to add
     */
    public DataSetRow getData() {
    	if (getRows().isEmpty()) {
    		return null;
    	}
    	return getRows().get(0);
    }

	/**
     * Gets a Data Element from this DataSet
     * @param key - The column key to add this element to
     * @param dataElement - The data to add
     */
    public Object getData(DataSetColumn column) {
    	if (getRows().isEmpty()) {
    		return null;
    	}
    	return getRows().get(0).getColumnValue(column);
    }
    
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getData().toString();
	}
    
    //****** PROPERTY ACCESS ******

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
}
