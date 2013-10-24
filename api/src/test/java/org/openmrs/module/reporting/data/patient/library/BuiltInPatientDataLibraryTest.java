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

package org.openmrs.module.reporting.data.patient.library;

import org.junit.Test;
import org.openmrs.Cohort;
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

    private void test(PatientDataDefinition definition, Object expectedValue) throws EvaluationException {
        Cohort cohort = new Cohort(Arrays.asList(7));

        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(cohort);

        EvaluatedPatientData data = pds.evaluate(definition, context);
        Object actualValue = data.getData().get(7);
        assertThat(actualValue, is(expectedValue));
    }

}
