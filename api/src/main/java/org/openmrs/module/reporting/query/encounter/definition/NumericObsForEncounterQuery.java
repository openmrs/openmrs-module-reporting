/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.encounter.definition;

import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Query that returns Encounters within which an Obs with the given Concept question is recorded
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class NumericObsForEncounterQuery extends ObsForEncounterQuery implements EncounterQuery {

	//***** PROPERTIES *****

	@ConfigurationProperty
	private RangeComparator operator1;

    @ConfigurationProperty
    private Double value1;

	@ConfigurationProperty
	private RangeComparator operator2;

	@ConfigurationProperty
	private Double value2;

	//***** PROPERTY ACCESS *****

	public RangeComparator getOperator1() {
		return operator1;
	}

	public void setOperator1(RangeComparator operator1) {
		this.operator1 = operator1;
	}

	public Double getValue1() {
		return value1;
	}

	public void setValue1(Double value1) {
		this.value1 = value1;
	}

	public RangeComparator getOperator2() {
		return operator2;
	}

	public void setOperator2(RangeComparator operator2) {
		this.operator2 = operator2;
	}

	public Double getValue2() {
		return value2;
	}

	public void setValue2(Double value2) {
		this.value2 = value2;
	}
}
