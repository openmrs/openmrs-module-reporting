/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
