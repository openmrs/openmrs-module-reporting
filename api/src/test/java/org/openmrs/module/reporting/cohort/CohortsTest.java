/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort;

import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.junit.Assert.assertThat;
import static org.openmrs.module.reporting.common.ReportingMatchers.isCohortWithExactlyIds;

public class CohortsTest extends BaseModuleContextSensitiveTest {

    @Test
    public void testAllPatients() throws Exception {
        assertThat(Cohorts.allPatients(null), isCohortWithExactlyIds(2, 6, 7, 8));
    }

    @Test
    public void testMales() throws Exception {
        assertThat(Cohorts.males(null), isCohortWithExactlyIds(2, 6));
    }

    @Test
    public void testFemales() throws Exception {
        assertThat(Cohorts.females(null), isCohortWithExactlyIds(7, 8));
    }

}