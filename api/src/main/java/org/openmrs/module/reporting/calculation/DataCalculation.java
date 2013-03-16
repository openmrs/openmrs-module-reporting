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
package org.openmrs.module.reporting.calculation;

import org.openmrs.calculation.BaseCalculation;
import org.openmrs.calculation.Calculation;
import org.openmrs.calculation.parameter.ParameterDefinitionSet;
import org.openmrs.module.reporting.data.DataDefinition;

/**
 * Adapter class which exposes a Data Definition as a Calculation
 */
public abstract class DataCalculation extends BaseCalculation {
	
	private DataDefinition dataDefinition;
	
	/**
	 * @see Calculation#getParameterDefinitionSet()
	 */
	@Override
	public ParameterDefinitionSet getParameterDefinitionSet() {
		return ReportingCalculationUtil.getParameterDefinitionSet(dataDefinition);
	}
	
	/**
	 * @return the dataDefinition
	 */
	public DataDefinition getDataDefinition() {
		return dataDefinition;
	}
	
	/**
	 * @param dataDefinition the dataDefinition to set
	 */
	public void setDataDefinition(DataDefinition dataDefinition) {
		this.dataDefinition = dataDefinition;
	}
}
