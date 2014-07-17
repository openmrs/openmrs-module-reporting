/*
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
