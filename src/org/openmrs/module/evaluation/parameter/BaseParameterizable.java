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
package org.openmrs.module.evaluation.parameter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsMetadata;

/**
 * Provides a Base implementation of the Parameterizable interface
 */
@SuppressWarnings("unchecked")
public abstract class BaseParameterizable extends BaseOpenmrsMetadata implements Parameterizable {

	private static Log log = LogFactory.getLog(BaseParameterizable.class);
	private static final long serialVersionUID = 1L;
	
	//***********************
	// PROPERTIES
	//***********************

	private List<Parameter> parameters = new ArrayList<Parameter>();
	
	//***********************
	// CONSTRUCTORS
	//***********************
	
	/**
	 * Default constructor
	 */
	public BaseParameterizable() {
		super();
	}
	
	//***********************
	// INSTANCE METHODS
	//***********************
	
		/**
	 * @see Parameterizable#addParameter(Parameter)
	 */
	public void addParameter(Parameter parameter) {
		if (parameters == null) {
			parameters = new ArrayList<Parameter>();
		}
		parameters.add(parameter);
	}
	
	/**
	 * @see Parameterizable#getParameter(String)
	 */
	public Parameter getParameter(String name) {
		if (parameters != null) {
			for (Parameter p : parameters) {
				if (p.getName().equalsIgnoreCase(name)) {
					return p;
				}
			}
		}
		return null;
	}
	
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * @return the parameters
	 */
	public List<Parameter> getParameters() {
		if (parameters == null) { 
			parameters = new ArrayList<Parameter>();
		}
		return parameters;
	}
	

	
}
