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
import org.openmrs.module.reporting.cohort.definition.DrugOrderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.library.BuiltInCohortDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Basic set of cohort definitions
 */
@Component
public class BuiltInCohortDefinitionLibrary1_10 extends BuiltInCohortDefinitionLibrary {

    
    
    @DocumentedDefinition("drugOrderSearch")
    public CohortDefinition getDrugOrderSearch() {
        CohortDefinition drugOrderCohortDefinition = new DrugOrderCohortDefinition();
        drugOrderCohortDefinition.addParameter(new Parameter("which", "reporting.parameter.which", Match.class));
        drugOrderCohortDefinition.addParameter(new Parameter("drugConcepts", "reporting.parameter.drugConcepts", Concept.class, List.class, null));
        drugOrderCohortDefinition.addParameter(new Parameter("drugSets", "reporting.parameter.drugSets", Concept.class, List.class, null));
        drugOrderCohortDefinition.addParameter(new Parameter("activatedOnOrBefore", "reporting.parameter.activatedOnOrBefore", Date.class));
        drugOrderCohortDefinition.addParameter(new Parameter("activatedOnOrAfter", "reporting.parameter.activatedOnOrAfter", Date.class));
        drugOrderCohortDefinition.addParameter(new Parameter("activeOnOrBefore", "reporting.parameter.activeOnOrBefore", Date.class));
        drugOrderCohortDefinition.addParameter(new Parameter("activeOnOrAfter", "reporting.parameter.activeOnOrAfter", Date.class));
        drugOrderCohortDefinition.addParameter(new Parameter("activeOnDate", "reporting.parameter.activeOnDate", Date.class));
        drugOrderCohortDefinition.addParameter(new Parameter("careSetting", "reporting.parameter.careSetting", CareSetting.class));
        drugOrderCohortDefinition.addParameter(new Parameter("drugs", "reporting.parameter.drugs", Drug.class, List.class, null));
        return drugOrderCohortDefinition;
    }
}
