/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.evaluation.parameter;

import java.util.List;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * A class that implements this interface indicates that it is 
 * capable of being configured at runtime using Parameters.
 * 
 * @see Parameter
 * @see EvaluationContext
 * @see ConfigurationProperty
 */
public interface Parameterizable extends OpenmrsMetadata {
	
	/**
	 * @return list of parameters that have been configured on this instance
	 */
	public List<Parameter> getParameters();
	
	/**
	 * @return the Parameter which has been added with the given name
	 */
	public Parameter getParameter(String name);
	
	/**
	 * This method take a Parameter as input and adds it to its list of Parameters
	 * @param parameter - The {@link Parameter} to add
	 */
	public void addParameter(Parameter parameter);

	/**
	 * This method take a Parameter as input and removes it from its list of Parameter.
	 * @param parameter 
	 * 		the {@link Parameter} to remove
	 */
	public void removeParameter(Parameter parameter);
	
	/**
	 * This method take a Parameter as input and removes it from its list of Parameters.
	 * @param parameterName 
	 * 		the name of the {@link Parameter} to remove
	 */
	public void removeParameter(String parameterName);
	
}
