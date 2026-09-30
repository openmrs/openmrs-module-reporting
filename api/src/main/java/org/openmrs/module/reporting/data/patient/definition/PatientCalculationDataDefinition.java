/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
