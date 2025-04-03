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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.module.reporting.common.Match;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DrugOrderCohortDefinition;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.reporting.common.ReportingMatchers.hasParameter;

/**
 *
 */
public class BuiltInCohortDefinitionLibrary1_10Test {

    private BuiltInCohortDefinitionLibrary1_10 library;

    @Before
    public void setUp() throws Exception {
        library = new BuiltInCohortDefinitionLibrary1_10();
    }

    @Test
    public void testgetDrugOrderSearch() throws Exception {
        CohortDefinition drugOrderCohortDefinition = library.getDrugOrderSearch();
        assertTrue(DrugOrderCohortDefinition.class.isAssignableFrom(drugOrderCohortDefinition.getClass()));
        assertThat(drugOrderCohortDefinition, hasParameter("which", Match.class));
        assertThat(drugOrderCohortDefinition, hasParameter("drugConcepts", Concept.class, List.class));
        assertThat(drugOrderCohortDefinition, hasParameter("drugSets", Concept.class, List.class));
        assertThat(drugOrderCohortDefinition, hasParameter("activatedOnOrBefore", Date.class));
        assertThat(drugOrderCohortDefinition, hasParameter("activatedOnOrAfter", Date.class));
        assertThat(drugOrderCohortDefinition, hasParameter("activeOnOrBefore", Date.class));
        assertThat(drugOrderCohortDefinition, hasParameter("activeOnOrAfter", Date.class));
        assertThat(drugOrderCohortDefinition, hasParameter("activeOnDate", Date.class));
        assertThat(drugOrderCohortDefinition, hasParameter("careSetting", CareSetting.class));
        assertThat(drugOrderCohortDefinition, hasParameter("drugs", Drug.class, List.class));
    }

}
