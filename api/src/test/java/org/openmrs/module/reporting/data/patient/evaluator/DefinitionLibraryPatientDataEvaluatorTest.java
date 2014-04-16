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
