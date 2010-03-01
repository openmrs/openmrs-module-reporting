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
package org.openmrs.module.reporting.evaluation.parameter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;

/**
 * Provides a Wrapper class for a Parameterizable instance, which
 * includes a Mapping of Parameter Name to either object values, or
 * expressions that reference parameters in the enclosing class.
 * @see EvaluationContext
 * @see EvaluationUtil
 */
public class Mapped<T extends Parameterizable> implements Serializable {

	private static final long serialVersionUID = 1L;
	private transient final Log log = LogFactory.getLog(getClass());
	
	//***********************
	// PROPERTIES
	//***********************
	
	private T parameterizable;
	private Map<String, Object> parameterMappings;
	
	//***********************
	// CONSTRUCTORS
	//***********************
	
	/**
	 * Default Constructor
	 */
	public Mapped() {
		super();
		parameterMappings = new HashMap<String, Object>();
	}
	
	/**
	 * Constructor which allows you to set all available Map<String, Object>
	 */
	public Mapped(T parameterizable, Map<String, Object> parameterMappings) {
		this();
		this.parameterizable = parameterizable;
		if (parameterMappings != null) {
			this.parameterMappings = parameterMappings;
		}
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
				try {
					Object evaluated = EvaluationUtil.evaluateExpression(s, getParameterMappings(), String.class);
					if (evaluated != null) {
						s = evaluated.toString();
					}
				}
				catch (Exception e) {
					log.warn("Error evaluating expression.", e);
				}
				return s;
			}
		}
		return "";
	}
	
	/**
	 * Adds a new Parameter Mapping to this wrapper class
	 * @param parameterName - The name of the Parameter to wrap
	 * @param expression - The expression which Maps to a Parameter Name in the enclosing class
	 */
	public void addParameterMapping(String parameterName, Object valueOrExpression) {
		parameterMappings.put(parameterName, valueOrExpression);
	}
	
	/** 
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Mapped) {
			Mapped<?> m = (Mapped<?>) obj;
			if (m.getParameterizable() != null && m.getParameterizable().equals(this.getParameterizable())) {
				Set<String> keys = new HashSet<String>(m.getParameterMappings().keySet());
				keys.addAll(this.getParameterMappings().keySet());
				if (m.getParameterMappings().size() == keys.size() && this.getParameterMappings().size() == keys.size()) {
					for (String key : keys) {
						if (!m.getParameterMappings().get(key).equals(this.getParameterMappings().get(key))) {
							return false;
						}
					}
					return true;
				}
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
		hash = hash * getParameterMappings().hashCode();
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
	public Map<String, Object> getParameterMappings() {
		if (parameterMappings == null) {
			parameterMappings = new HashMap<String, Object>();
		}
		return parameterMappings;
	}

	/**
	 * @param parameterMappings the parameterMappings to set
	 */
	public void setParameterMappings(Map<String, Object> parameterMappings) {
		this.parameterMappings = parameterMappings;
	}
}
