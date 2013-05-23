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


package org.openmrs.module.reporting.data.patient.definition;

import org.openmrs.calculation.CalculationRegistration;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Data definition based on {@link org.openmrs.calculation.patient.PatientCalculation}
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.PatientCalculationDataDefinition")
public class PatientCalculationDataDefinition extends BaseDataDefinition implements PatientDataDefinition {

	@ConfigurationProperty(required = true)
	private CalculationRegistration calculationRegistration;

	/**
	 * Default Constructor
	 */
	public PatientCalculationDataDefinition() {
		super();
	}

	/**
	 * Name constructor
	 */
	public PatientCalculationDataDefinition(String name) {
		super(name);
	}

	/**
	 * Name and content constructor
	 *
	 * @param name                    the name of the data definition
	 * @param calculationRegistration the calculation registration for the data definition
	 */
	public PatientCalculationDataDefinition(String name, CalculationRegistration calculationRegistration) {
		super(name);
		this.calculationRegistration = calculationRegistration;
	}

	/**
	 * @return the associated calculation registration
	 */
	public CalculationRegistration getCalculationRegistration() {
		return calculationRegistration;
	}

	/**
	 * @param calculationRegistration the calculation registration to set on this definition
	 */
	public void setCalculationRegistration(CalculationRegistration calculationRegistration) {
		this.calculationRegistration = calculationRegistration;
	}

	/**
	 * @see org.openmrs.module.reporting.data.BaseDataDefinition#getDataType()
	 */
	@Override
	public Class<?> getDataType() {
		return CalculationResult.class;
	}
}
