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

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a Wrapper class for a Parameterizable instance, which
 * includes a Mapping of Parameter Name to Expressions in order to
 * to Parameters in an enclosing class.
 */
public class Mapped<T extends Parameterizable>  {

	private static final long serialVersionUID = 1L;
	
	//***********************
	// PROPERTIES
	//***********************
	
	private T parameterizable;
	private Map<String, String> parameterMappings;
	
	//***********************
	// CONSTRUCTORS
	//***********************
	
	/**
	 * Default Constructor
	 */
	public Mapped() {
		super();
		parameterMappings = new HashMap<String, String>();
	}
	
	/**
	 * Constructor which allows you to set all available Map<String, String>
	 */
	public Mapped(T parameterizable, Map<String, String> parameterMappings) {
		this();
		this.parameterizable = parameterizable;
		if (parameterMappings != null) {
			this.parameterMappings = parameterMappings;
		}
	}
	
	/**
	 * Constructor which allows you to set all available properties
	 */
	public Mapped(T parameterizable, String parameterMappings) {
		this();
		this.parameterizable = parameterizable;
		setParameterMappings(parameterMappings);
	}
	
	//***********************
	// INSTANCE METHODS
	//***********************
	
	/**
	 * Convenience method that enables passing parameter mappings as a String.
	 * Entries are separated by <code>,</code> and keys are separated from values by <code>=</code>
	 */
	public void setParameterMappings(String mappingsAsString) throws ParameterException {
		parameterMappings = new HashMap<String, String>();
		if (mappingsAsString != null) {
			try {
				String[] split = mappingsAsString.split(",");
				for (int i=0; i<split.length; i++) {
					String[] keyVal = split[i].split("=");
					parameterMappings.put(keyVal[0], keyVal[1]);
				}
			}
			catch (Exception e) {
				throw new ParameterException("Error while setting parameter mappings from String", e);
			}
		}
	}
	
	/**
	 * Adds a new Parameter Mapping to this wrapper class
	 * @param parameterName - The name of the Parameter to wrap
	 * @param expression - The expression which Maps to a Parameter Name in the enclosing class
	 */
	public void addParameterMapping(String parameterName, String expression) {
		if (parameterMappings == null) {
			parameterMappings = new HashMap<String, String>();
		}
		parameterMappings.put(parameterName, expression);
	}
	
	//***********************
	// PROPERTY ACCESS
	//***********************

	/**
	 * @return the parameterizable
	 */
	public T getParameterizable() {
		return parameterizable;
	}

	/**
	 * @param parameterizable the parameterizable to set
	 */
	public void setParameterizable(T parameterizable) {
		this.parameterizable = parameterizable;
	}

	/**
	 * @return the parameterMappings
	 */
	public Map<String, String> getParameterMappings() {
		return parameterMappings;
	}

	/**
	 * @param parameterMappings the parameterMappings to set
	 */
	public void setParameterMappings(Map<String, String> parameterMappings) {
		this.parameterMappings = parameterMappings;
	}
}
