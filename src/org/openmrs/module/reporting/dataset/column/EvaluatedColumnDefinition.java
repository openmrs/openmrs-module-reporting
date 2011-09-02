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
package org.openmrs.module.reporting.dataset.column;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Encapsulates the results of Evaluating a ColumnDefinition
 */
public class EvaluatedColumnDefinition implements Evaluated<ColumnDefinition> {
	
	//***** PROPERTIES *****
	
    private ColumnDefinition definition;
    private EvaluationContext context;
    private Map<Integer, Object> columnValues;
    
    //***** CONSTRUCTORS *****
    
    public EvaluatedColumnDefinition() {
    	super();
    }
    
    public EvaluatedColumnDefinition(ColumnDefinition definition, EvaluationContext context) {
    	this.definition = definition;
    	this.context = context;
    }
    
    //***** PROPERTY ACCESS *****
    
	/**
	 * @return the definition
	 */
	public ColumnDefinition getDefinition() {
		return definition;
	}
	
	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(ColumnDefinition definition) {
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
	 * @return the columnValues
	 */
	public Map<Integer, Object> getColumnValues() {
		if (columnValues == null) {
			columnValues = new HashMap<Integer, Object>();
		}
		return columnValues;
	}

	/**
	 * @param columnValues the columnValues to set
	 */
	public void setColumnValues(Map<Integer, Object> columnValues) {
		this.columnValues = columnValues;
	}
	
	/**
	 * Adds a Column Value
	 */
	public void addColumnValue(Integer id, Object value) {
		getColumnValues().put(id, value);
	}
	
	/**
	 * Adds a Column Value
	 */
	public void retainColumnValues(Collection<Integer> ids) {
		getColumnValues().keySet().retainAll(ids);
	}
}
