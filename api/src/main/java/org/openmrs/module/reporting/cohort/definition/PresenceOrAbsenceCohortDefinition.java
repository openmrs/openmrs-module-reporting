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
package org.openmrs.module.reporting.cohort.definition;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows for configuring one or more "cohortsToCheck".
 * If "presentInAtLeast" is set, it will include those patients who are present in that number of cohortsToCheck or more
 * If "presentInAtMost is set, it will include those patients who are present in that number of cohortsToCheck or fewer
 */
@Localized("reporting.PresenceOrAbsenceCohortDefinition")
public class PresenceOrAbsenceCohortDefinition extends BaseCohortDefinition {

	//***** CONFIGURATION PROPERTIES *****

	@ConfigurationProperty
	private List<Mapped<? extends CohortDefinition>> cohortsToCheck;

	@ConfigurationProperty
	private Integer presentInAtLeast;

	@ConfigurationProperty
	private Integer presentInAtMost;

	//***** PROPERTY ACCESS *****

	public List<Mapped<? extends CohortDefinition>> getCohortsToCheck() {
		return cohortsToCheck;
	}

	public void setCohortsToCheck(List<Mapped<? extends CohortDefinition>> cohortsToCheck) {
		this.cohortsToCheck = cohortsToCheck;
	}

	public void addCohortToCheck(Mapped<? extends CohortDefinition> cohortDefinition) {
		if (cohortsToCheck == null) {
			cohortsToCheck = new ArrayList<Mapped<? extends CohortDefinition>>();
		}
		cohortsToCheck.add(cohortDefinition);
	}

	public Integer getPresentInAtLeast() {
		return presentInAtLeast;
	}

	public void setPresentInAtLeast(Integer presentInAtLeast) {
		this.presentInAtLeast = presentInAtLeast;
	}

	public Integer getPresentInAtMost() {
		return presentInAtMost;
	}

	public void setPresentInAtMost(Integer presentInAtMost) {
		this.presentInAtMost = presentInAtMost;
	}
}
