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
package org.openmrs.module.reporting.dataset.definition;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This DataSetDefinition wraps a {@link org.openmrs.module.reporting.dataset.definition.DataSetDefinition} and allows you to run it on
 * a series of parameters. The {@link org.openmrs.module.reporting.dataset.DataSet} that this defines has the same columns
 * as the wrapped {@link org.openmrs.module.reporting.dataset.definition.DataSetDefinition}, prepended by columns for each added iteration parameter.
 * Each row of the DataSet is the result you'd get from evaluating the wrapped
 * DataSetDefinition on one supplied parameters.
 */
@Localized("reporting.MultiParameterDataSetDefinition")
public class MultiParameterDataSetDefinition extends BaseDataSetDefinition implements DataSetDefinition {

	@ConfigurationProperty
	private DataSetDefinition baseDefinition;

	@ConfigurationProperty
	private List<Map<String, Object>> iterations;

	public MultiParameterDataSetDefinition() {
		iterations = new ArrayList<Map<String, Object>>();
	}

	public MultiParameterDataSetDefinition(DataSetDefinition baseDefinition) {
		this();
		this.baseDefinition = baseDefinition;
	}
	
	public void addIteration(Map<String, Object> iteration) {
        if (iterations == null) {
            iterations = new ArrayList<Map<String, Object>>();
        }
		iterations.add(iteration);
	}

	public DataSetDefinition getBaseDefinition() {
		return baseDefinition;
	}
	
	public void setBaseDefinition(DataSetDefinition baseDefinition) {
		this.baseDefinition = baseDefinition;
	}

	public List<Map<String, Object>> getIterations() {

		return iterations;
	}

	public void setIterations(List<Map<String, Object>> iterations) {
		//since XML deserialization creates one instance of map if all keys are the same
		//we need to ensure each iteration gets it's own instance of map (since it's desired behaviour)
		List<Map<String, Object>> newIterations = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> iteration: iterations) {
			newIterations.add(new HashMap<String, Object>(iteration));
		}
		this.iterations = newIterations;
	}
}
