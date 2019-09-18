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
/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ConditionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.library.BuiltInCohortDefinitionLibrary2_2;

import static org.openmrs.module.reporting.common.ReportingMatchers.hasParameter;


public class BuiltInCohortDefinitionLibrary2_2Test {

    private BuiltInCohortDefinitionLibrary2_2 library;

    @Before
    public void setUp() throws Exception {
        library = new BuiltInCohortDefinitionLibrary2_2();
    }

    @Test
	public void testGetConditonSearchAdavanced() throws Exception {
		CohortDefinition cd = library.getConditonSearchAdvanced();
		assertTrue(ConditionCohortDefinition.class.isAssignableFrom(cd.getClass()));
		assertThat(cd, hasParameter("onsetDateOnOrBefore", Date.class));
	    assertThat(cd, hasParameter("onsetDateOnOrAfter", Date.class));
	    assertThat(cd, hasParameter("endDateOnOrBefore", Date.class));
	    assertThat(cd, hasParameter("endDateOnOrAfter", Date.class));
	    assertThat(cd, hasParameter("createdOnOrBefore", Date.class));
	    assertThat(cd, hasParameter("createdOnOrAfter", Date.class));
	    assertThat(cd, hasParameter("activeOnDate", Date.class));
	    assertThat(cd, hasParameter("conditionNonCoded", String.class));
	    assertThat(cd, hasParameter("conditionCoded", Concept.class));
	}  
}
