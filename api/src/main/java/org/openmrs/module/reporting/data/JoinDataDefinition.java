/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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