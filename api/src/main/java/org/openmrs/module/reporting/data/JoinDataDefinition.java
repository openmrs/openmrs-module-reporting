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

import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.BaseDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Adapter class for exposing one class of Data as applicable for another class of Data
 * For example, a PersonDataDefinition is applicable for both persons and patients - an subclass of this
 * would implement PatientDataDefinition and accept a PersonDataDefinition as a "one-to-many" join
 */
public abstract class JoinDataDefinition<T extends DataDefinition> extends BaseDataDefinition {
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
	private T joinedDefinition;
	
	/**
	 * Default Constructor
	 */
	public JoinDataDefinition() {
		super();
	}
	
	/**
	 * Default Constructor
	 */
	public JoinDataDefinition(T joinedDefinition) {
		this.joinedDefinition = joinedDefinition;
	}
	
	/**
	 * Constructor including data definition name
	 */
	public JoinDataDefinition(String name, T joinedDefinition) {
		super(name);
		this.joinedDefinition = joinedDefinition;
	}

	//***** INSTANCE METHODS *****
	
	/**
	 * @return the Joined DataDefinition type that this operates against
	 */
	public abstract Class<T> getJoinedDefinitionType();
	
	/** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		if (joinedDefinition != null) {
			return joinedDefinition.getDataType();
		}
		return Object.class;
	}
	
	/**
	 * @see BaseDefinition#getParameter(String)
	 */
	@Override
	public Parameter getParameter(String name) {
		if (joinedDefinition != null) {
			return joinedDefinition.getParameter(name);
		}
		return null;
	}

	/**
	 * @see BaseDefinition#getParameters()
	 */
	@Override
	public List<Parameter> getParameters() {
		if (joinedDefinition == null) {
			return new ArrayList<Parameter>();
		}
		return joinedDefinition.getParameters();
	}
	
	/**
	 * @see org.openmrs.BaseOpenmrsMetadata#getName()
	 */
	@Override
	public String getName() {
        if (super.getName() != null) {
            return super.getName();
        } else if (joinedDefinition != null) {
	    	return joinedDefinition.getName();
	    }
	    return null;
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the joinedDefinition
	 */
	public T getJoinedDefinition() {
		return joinedDefinition;
	}

	/**
	 * @param joinedDefinition the joinedDefinition to set
	 */
	public void setJoinedDefinition(T joinedDefinition) {
		this.joinedDefinition = joinedDefinition;
	}
}