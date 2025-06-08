/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.visit.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.VisitData;
import org.openmrs.module.reporting.data.visit.definition.PatientToVisitDataDefinition;
import org.openmrs.module.reporting.data.visit.service.VisitDataService;
import org.openmrs.module.reporting.evaluation.context.VisitEvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.visit.VisitIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PatientToVisitDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    PatientService patientService;

    @Autowired @Qualifier("reportingVisitDataService")
    VisitDataService visitDataService;

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
    public void evaluate_shouldReturnPatientDataForEachVisitInThePassedContext() throws Exception {

        PatientIdentifierType pit = patientService.getPatientIdentifierType(2);
        PatientIdentifierDataDefinition pidd = new PatientIdentifierDataDefinition();
        pidd.setIncludeFirstNonNullOnly(true);
        pidd.addType(pit);

        PatientToVisitDataDefinition d = new PatientToVisitDataDefinition(pidd);
        VisitEvaluationContext context = new VisitEvaluationContext();
        context.setBaseVisits(new VisitIdSet(1, 2, 4));     // in our demo set include two visits for same patient
        EvaluatedVisitData ed = Context.getService(VisitDataService.class).evaluate(d, context);

        assertThat(ed.getData().size(), is(3));
        assertThat((PatientIdentifier) ed.getData().get(1), is(patientService.getPatient(2).getPatientIdentifier(pit)));
        assertThat((PatientIdentifier) ed.getData().get(2), is(patientService.getPatient(2).getPatientIdentifier(pit)));
        assertThat((PatientIdentifier) ed.getData().get(4), is(patientService.getPatient(6).getPatientIdentifier(pit)));

    }

    @Test
    public void evaluate_shouldReturnEmptySetIfInputSetEmpty() throws Exception {

        PatientIdentifierType pit = patientService.getPatientIdentifierType(2);
        PatientIdentifierDataDefinition pidd = new PatientIdentifierDataDefinition();
        pidd.setIncludeFirstNonNullOnly(true);
        pidd.addType(pit);

        PatientToVisitDataDefinition d = new PatientToVisitDataDefinition(pidd);
        VisitEvaluationContext context = new VisitEvaluationContext();
        context.setBaseVisits(new VisitIdSet());
        EvaluatedVisitData ed = Context.getService(VisitDataService.class).evaluate(d, context);

        assertThat(ed.getData().size(), is(0));
    }

    @Test
    public void evaluate_shouldProperlyPassParametersThroughToNestedDefinition() throws Exception {

        PatientToVisitDataDefinition visitDef = new PatientToVisitDataDefinition();

        PatientIdentifierDataDefinition pidd = new PatientIdentifierDataDefinition();
        pidd.setIncludeFirstNonNullOnly(true);
        pidd.addParameter(new Parameter("types", "Types", PatientIdentifierType.class, List.class, null, null));

        visitDef.setJoinedDefinition(pidd);

        VisitEvaluationContext context = new VisitEvaluationContext();
        PatientIdentifierType pit = patientService.getPatientIdentifierType(2);
        context.addParameterValue("types", Arrays.asList(pit));

        context.setBaseVisits(new VisitIdSet(1, 4));

        VisitData data = visitDataService.evaluate(visitDef, context);
        System.out.println(data.getData());

        PatientIdentifier id1 = (PatientIdentifier) data.getData().get(1);
        PatientIdentifier id2 = (PatientIdentifier) data.getData().get(4);

        assertThat(id1, is(patientService.getPatient(2).getPatientIdentifier(pit)));
        assertThat(id2, is(patientService.getPatient(6).getPatientIdentifier(pit)));
    }
}
