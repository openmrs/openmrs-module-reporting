/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition.library;

import org.openmrs.Concept;
import org.openmrs.CareSetting;
import org.openmrs.Drug;
import org.openmrs.module.reporting.common.Match;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ConditionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.library.BuiltInCohortDefinitionLibrary2_2;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class BuiltInCohortDefinitionLibrary2_2 extends BuiltInCohortDefinitionLibrary {
	
	@DocumentedDefinition("conditonSearchAdvanced")
	public CohortDefinition getConditonSearchAdvanced() {
		ConditionCohortDefinition cd = new ConditionCohortDefinition();
		cd.addParameter(new Parameter("conditionCoded", "reporting.parameter.conditionCoded", Concept.class));
		cd.addParameter(new Parameter("conditionNonCoded", "reporting.parameter.conditionNonCoded", String.class));
		cd.addParameter(new Parameter("onsetDateOnOrBefore", "reporting.parameter.onsetDateOnOrBefore", Date.class));
		cd.addParameter(new Parameter("onsetDateOnOrAfter", "reporting.parameter.onsetDateOnOrAfter", Date.class));
		cd.addParameter(new Parameter("endDateOnOrBefore", "reporting.parameter.endDateOnOrBefore", Date.class));
		cd.addParameter(new Parameter("endDateOnOrAfter", "reporting.parameter.endDateOnOrAfter", Date.class));
		cd.addParameter(new Parameter("createdOnOrBefore", "reporting.parameter.createdOnOrBefore", Date.class));
		cd.addParameter(new Parameter("createdOnOrAfter", "reporting.parameter.createdOnOrAfter", Date.class));
		cd.addParameter(new Parameter("activeOnDate", "reporting.parameter.activeOnDate", Date.class));
		return cd;
	}
}
