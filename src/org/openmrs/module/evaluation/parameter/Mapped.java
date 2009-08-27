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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

/**
 * Provides a Wrapper class for a Parameterizable instance, which
 * includes a Mapping of Parameter Name to Expressions in order to
 * to Parameters in an enclosing class.
 */
public class Mapped<T extends Parameterizable> implements Serializable {

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
	 * Returns a description that fills in any parameter names in the description
	 * of the {@link Parameterizable}, with values from the populated mappings.  For example, 
	 * if the underlying {@link Parameterizable} had a description of 
	 * Patients in the program on ${effectiveDate}, and a parameter mapping which mapped effectiveDate to
	 * 01/01/2009, the returned description would read Patients in the program on 01/01/2009
	 */
	public String getDescription() {
		if (parameterizable != null) {
			String s = parameterizable.getDescription();
			if (StringUtils.isNotEmpty(s)) {
				for (String from : getParameterMappings().keySet()) {
					String to = getParameterMappings().get(from);
					s = s.replace("${"+from+"}", to);
				}
				return s;
			}
		}
		return "";
	}
	
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
					if (keyVal.length > 1) { // sanity check
						parameterMappings.put(keyVal[0], keyVal[1]);
					}
				}
			}
			catch (Exception e) {
				throw new ParameterException("Error while setting parameter mappings from String", e);
			}
		}
	}
	
	/**
	 * Convenience method that returns the parameter mappings as String
	 * This will return parameters in order of key, with each entry separated by a <code>,</code>
	 * and each key/value pair separated by a <code>=</code>
	 * @return a String representation of the parameter mappings
	 */
	public String getParameterMappingsAsString() {
		StringBuilder ret = new StringBuilder();
		if (parameterMappings != null) {
			Map<String, String> sortedMappings = new TreeMap<String, String>(parameterMappings);
			for (Iterator<String> i = sortedMappings.keySet().iterator(); i.hasNext();) {
				String key = i.next();
				ret.append(key + "=" + sortedMappings.get(key) + (i.hasNext() ? "," : ""));
			}
		}
		return ret.toString();
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
	
	/** 
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Mapped) {
			Mapped<?> m = (Mapped<?>) obj;
			if (m.getParameterizable() != null && m.getParameterizable().equals(this.getParameterizable())) {
				return m.getParameterMappingsAsString().equals(this.getParameterMappingsAsString());
			}
		}
		return this == obj;
	}

	/** 
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 8;
		hash = (parameterizable == null ? hash : hash * parameterizable.hashCode());
		hash = hash * getParameterMappingsAsString().hashCode();
		return 31 * hash;
	}

	/** 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getDescription();
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
