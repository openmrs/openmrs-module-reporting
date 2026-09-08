/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;

/**
 * Provides a Base implementation of the Definition interface
 */
public abstract class BaseDefinition extends BaseOpenmrsMetadata implements Definition {

	protected static Log log = LogFactory.getLog(BaseDefinition.class);
	public static final long serialVersionUID = 1L;
	
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
	public BaseDefinition() {
		super();
	}
	
	//***********************
	// INSTANCE METHODS
	//***********************
	
	
	/**
	 * Add a list of parameters.
	 */
	public void addParameters(List<Parameter> parameters) { 
		for (Parameter parameter : parameters) { 
			addParameter(parameter);
		}		
	}
	
	
	/**
	 * @see Parameterizable#addParameter(Parameter)
	 */
	public void addParameter(Parameter parameter) {
		if (parameters == null) {
			parameters = new ArrayList<Parameter>();
		}
		// Check for and remove duplicates
		Parameter existingParameter = getParameter(parameter.getName());
		if (existingParameter!=null)
			removeParameter(existingParameter);
		parameters.add(parameter);
	}
	
	/**
	 * @see Parameterizable#removeParameter(Parameter)
	 */
	public void removeParameter(Parameter parameter) {
		parameters.remove(parameter);
	}
	
	/**
	 * @see Parameterizable#removeParameter(Parameter)
	 */
	public void removeParameter(String parameterName) {
		Parameter parameter = getParameter(parameterName);
		if (parameter != null)
			parameters.remove(parameter);
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
	
	/** @see Object#equals(Object) */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Definition) {
			Definition def = (Definition) obj;
			if (this.getUuid() != null) {
				return (this.getUuid().equals(def.getUuid()));
			}
		}
		return this == obj;
	}
	
	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (getUuid() == null ? 0 : 31 * getUuid().hashCode());
	}
}
