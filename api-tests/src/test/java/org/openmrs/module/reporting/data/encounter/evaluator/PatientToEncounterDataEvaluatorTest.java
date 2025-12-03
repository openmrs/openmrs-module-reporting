/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.encounter.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.encounter.EncounterData;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.PatientToEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PatientToEncounterDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    PatientService patientService;

    @Autowired @Qualifier("reportingEncounterDataService")
    EncounterDataService encounterDataService;

    /**
     * Run this before each unit test in this class. The "@Before" method in
     * {@link org.openmrs.test.BaseContextSensitiveTest} is run right before this method.
     *
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    @Test
    public void evaluate_shouldReturnPatientDataForEachEncounterInThePassedContext() throws Exception {

        PatientIdentifierType pit = patientService.getPatientIdentifierType(2);
        PatientIdentifierDataDefinition pidd = new PatientIdentifierDataDefinition();
        pidd.setIncludeFirstNonNullOnly(true);
        pidd.addType(pit);

        PatientToEncounterDataDefinition d = new PatientToEncounterDataDefinition(pidd);
        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(8,9,10));     // in our demo set include two encounters for same patient
        EvaluatedEncounterData ed = Context.getService(EncounterDataService.class).evaluate(d, context);
        
        assertThat(ed.getData().size(), is(3));
        assertThat((PatientIdentifier) ed.getData().get(8), is(patientService.getPatient(21).getPatientIdentifier(pit)));
        assertThat((PatientIdentifier) ed.getData().get(9), is(patientService.getPatient(22).getPatientIdentifier(pit)));
        assertThat((PatientIdentifier) ed.getData().get(10), is(patientService.getPatient(22).getPatientIdentifier(pit)));

    }

    @Test
    public void evaluate_shouldReturnEmptySetIfInputSetEmpty() throws Exception {

        PatientIdentifierType pit = patientService.getPatientIdentifierType(2);
        PatientIdentifierDataDefinition pidd = new PatientIdentifierDataDefinition();
        pidd.setIncludeFirstNonNullOnly(true);
        pidd.addType(pit);

        PatientToEncounterDataDefinition d = new PatientToEncounterDataDefinition(pidd);
        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet());
        EvaluatedEncounterData ed = Context.getService(EncounterDataService.class).evaluate(d, context);

        assertThat(ed.getData().size(), is(0));
    }

    @Test
    public void evaluate_shouldProperlyPassParametersThroughToNestedDefinition() throws Exception {

        PatientToEncounterDataDefinition dataDef = new PatientToEncounterDataDefinition();

        PatientIdentifierDataDefinition pidd = new PatientIdentifierDataDefinition();
        pidd.setIncludeFirstNonNullOnly(true);
        pidd.addParameter(new Parameter("types", "Types", PatientIdentifierType.class, List.class, null, null));

        dataDef.setJoinedDefinition(pidd);

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        PatientIdentifierType pit = patientService.getPatientIdentifierType(1);
        context.addParameterValue("types", Arrays.asList(pit));

        context.setBaseEncounters(new EncounterIdSet(3));

        EncounterData data = encounterDataService.evaluate(dataDef, context);

        PatientIdentifier id1 = (PatientIdentifier) data.getData().get(3);
        Assert.assertEquals("6TS-4", id1.getIdentifier());
    }

}
