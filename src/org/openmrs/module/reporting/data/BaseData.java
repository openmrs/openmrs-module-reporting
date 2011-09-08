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
package org.openmrs.module.reporting.data;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Provides abstract implementation of the Data interface
 */
public abstract class BaseData<T extends DataDefinition> implements Evaluated<T>, Data {
	
	//***** PROPERTIES *****
	
    private T definition;
    private EvaluationContext context;
    private Map<Integer, Object> data;
    
    //***** CONSTRUCTORS *****
    
    public BaseData() {
    	super();
    }
    
    public BaseData(T definition, EvaluationContext context) {
    	this.definition = definition;
    	this.context = context;
    }
    
    //***** PROPERTY ACCESS *****

	/**
	 * @return the definition
	 */
	public T getDefinition() {
		return definition;
	}
	
	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(T definition) {
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
	public Map<Integer, Object> getData() {
		if (data == null) {
			data = new HashMap<Integer, Object>();
		}
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Map<Integer, Object> data) {
		this.data = data;
	}
}
