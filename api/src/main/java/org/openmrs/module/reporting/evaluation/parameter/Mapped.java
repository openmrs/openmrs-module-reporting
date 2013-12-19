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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provides a Wrapper class for a Parameterizable instance, which
 * includes a Mapping of Parameter Name to either object values, or
 * expressions that reference parameters in the enclosing class.
 * @see EvaluationContext
 * @see EvaluationUtil
 */
public class Mapped<T extends Parameterizable> implements Serializable {

	public static final long serialVersionUID = 1L;
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
    // STATIC FACTORY METHODS
    //***********************

    public static <T extends Parameterizable> Mapped<T> map(T parameterizable, String mappings) {
        return new Mapped(parameterizable, ParameterizableUtil.createParameterMappings(mappings));
    }

    public static <T extends Parameterizable> Mapped<T> noMappings(T parameterizable) {
        return new Mapped(parameterizable, null);
    }

    /**
     * Maps every parameter "straight through", e.g. "startDate -> ${startDate}"
     * @param parameterizable
     * @param <T>
     * @return
     */
    public static <T extends Parameterizable> Mapped<T> mapStraightThrough(T parameterizable) {
        return new Mapped(parameterizable, straightThroughMappings(parameterizable));
    }

    /**
     * @param parameterizable
     * @return a "straight through" Map of each parameter in parameterizable, e.g. "startDate" -> "${startDate}"
     */
    public static Map<String, Object> straightThroughMappings(Parameterizable parameterizable) {
        Map<String, Object> mappings = new HashMap<String, Object>();
        for (Parameter parameter : parameterizable.getParameters()) {
            mappings.put(parameter.getName(), "${" + parameter.getName() + "}");
        }
        return mappings;
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
					Object evaluated = EvaluationUtil.evaluateExpression(s, getParameterMappings());
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
	 * @param valueOrExpression - The expression which Maps to a Parameter Name in the enclosing class
	 */
	public void addParameterMapping(String parameterName, Object valueOrExpression) {
		parameterMappings.put(parameterName, valueOrExpression);
	}
	
	/** 
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Mapped<?>) {
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
		if (ObjectUtil.notNull(getDescription())) {
			return getDescription();
		}
		StringBuilder ret = new StringBuilder();
		if (parameterizable != null) {
			ret.append(parameterizable.toString());
			if (getParameterMappings() != null && !getParameterMappings().isEmpty()) {
				ret.append(" [").append(ObjectUtil.toString(getParameterMappings(), ",")).append("]");
			}
		}
		return ret.toString();
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
	
	/**
	 * if the parameterized object is an OpenmrsObject, return its uuid.
	 * @return
	 */
	public String getUuidOfMappedOpenmrsObject() {
		if (parameterizable != null && parameterizable instanceof OpenmrsObject) {
			OpenmrsObject o = (OpenmrsObject) parameterizable;
			return o.getUuid();
		}
		else {
			log.warn("Mapped.getUuidOfMappedOpenmrsObject is null or called for a mapped object that is not an OpenmrsObject.");
			return null;  //should this throw an exception instead?
		}
	}
}
