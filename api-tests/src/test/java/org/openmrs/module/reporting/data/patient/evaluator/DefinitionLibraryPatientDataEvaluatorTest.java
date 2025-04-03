/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.patient.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.DefinitionLibraryPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DefinitionLibraryPatientDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private PatientDataService service;

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    @Test
    public void testEvaluateWithNoParameters() throws Exception {
        DefinitionLibraryPatientDataDefinition def = new DefinitionLibraryPatientDataDefinition();
        def.setDefinitionKey(BuiltInPatientDataLibrary.PREFIX + "patientId");

        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort("7"));

        EvaluatedPatientData result = service.evaluate(def, context);
        assertThat(result.getData().size(), is(1));
        assertThat((Integer) result.getData().get(7), is(7));
    }

    @Test
    public void testEvaluateWithParameters() throws Exception {
        Map<String, Object> parameterValues = new HashMap<String, Object>();
        parameterValues.put("effectiveDate", DateUtil.parseYmd("2013-12-01"));

        DefinitionLibraryPatientDataDefinition def = new DefinitionLibraryPatientDataDefinition();
        def.setDefinitionKey(BuiltInPatientDataLibrary.PREFIX + "ageOnDate.fullYears");
        def.setParameterValues(parameterValues);

        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort("7"));

        EvaluatedPatientData result = service.evaluate(def, context);
        assertThat(result.getData().size(), is(1));
        assertThat((Integer) result.getData().get(7), is(37));
    }

    @Test
    public void testEvaluateWithParameterValuesFromContext() throws Exception {

        DefinitionLibraryPatientDataDefinition def = new DefinitionLibraryPatientDataDefinition();
        def.setDefinitionKey(BuiltInPatientDataLibrary.PREFIX + "ageAtStart");

        Date startDate = DateUtil.parseYmd("2013-12-01");

        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("startDate", startDate);
        context.setBaseCohort(new Cohort("7"));

        EvaluatedPatientData result = service.evaluate(def, context);
        assertThat(result.getData().size(), is(1));
		Age ageResult = (Age) result.getData().get(7);
        assertThat(ageResult.getBirthDate().getTime(), is(DateUtil.parseYmd("1976-08-25").getTime()));
		assertThat(ageResult.getCurrentDate(), is(startDate));
    }

}
