/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.patient.library;

import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class BuiltInPatientDataLibraryTest extends BaseModuleContextSensitiveTest {

    @Autowired
    PatientDataService pds;

    @Autowired
    BuiltInPatientDataLibrary library;

    @Test
    public void testPreferredFamilyName() throws Exception {
        test(library.getPreferredFamilyName(), "Chebaskwony");
    }

    @Test
    public void testBirthdate() throws Exception {
        test(library.getBirthdateYmd(), "1976-08-25");
    }

    @Test
    public void testAgeAtStart() throws Exception {
        // born 1976-08-25
        Age actual = (Age) eval(library.getAgeAtStart());
        assertThat(actual.getBirthDate().getTime(), is(DateUtil.parseYmd("1976-08-25").getTime()));
        assertThat(actual.getCurrentDate().getTime(), is(DateUtil.parseYmd("2013-01-01").getTime()));
    }

    @Test
    public void testAgeAtEnd() throws Exception {
        // born 1976-08-25
        Age actual = (Age) eval(library.getAgeAtEnd());
        assertThat(actual.getBirthDate().getTime(), is(DateUtil.parseYmd("1976-08-25").getTime()));
        assertThat(actual.getCurrentDate().getTime(), is(DateUtil.parseYmd("2013-12-31").getTime()));
    }

    private Object eval(PatientDataDefinition definition) throws EvaluationException {
        Cohort cohort = new Cohort(Arrays.asList(7));

        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(cohort);
        context.addParameterValue("startDate", DateUtil.parseYmd("2013-01-01"));
        context.addParameterValue("endDate", DateUtil.parseYmd("2013-12-31"));
        EvaluatedPatientData data = pds.evaluate(definition, context);
        return data.getData().get(7);
    }

    private void test(PatientDataDefinition definition, Object expectedValue) throws EvaluationException {
        Object actualValue = eval(definition);
        assertThat(actualValue, is(expectedValue));
    }

}
