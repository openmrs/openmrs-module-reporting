/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
