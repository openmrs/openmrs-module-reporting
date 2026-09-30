/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.obs.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.ObsData;
import org.openmrs.module.reporting.data.obs.definition.PatientToObsDataDefinition;
import org.openmrs.module.reporting.data.obs.service.ObsDataService;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.obs.ObsIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PatientToObsDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    PatientService patientService;

    @Autowired @Qualifier("reportingObsDataService")
    ObsDataService obsDataService;

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
    public void evaluate_shouldReturnPatientDataForEachObsInThePassedContext() throws Exception {

        PatientIdentifierType pit = patientService.getPatientIdentifierType(2);
        PatientIdentifierDataDefinition pidd = new PatientIdentifierDataDefinition();
        pidd.setIncludeFirstNonNullOnly(true);
        pidd.addType(pit);
        
        PatientToObsDataDefinition d = new PatientToObsDataDefinition(pidd);
        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseObs(new ObsIdSet(20, 26));
        EvaluatedObsData od = Context.getService(ObsDataService.class).evaluate(d, context);

        assertThat(od.getData().size(), is(2));
        assertThat((PatientIdentifier) od.getData().get(20), is(patientService.getPatient(21).getPatientIdentifier(pit)));
        assertThat((PatientIdentifier) od.getData().get(26), is(patientService.getPatient(22).getPatientIdentifier(pit)));

    }

    @Test
    public void evaluate_shouldReturnEmptySetIfInputObsIdSetIsEmpty() throws Exception {

        PatientIdentifierType pit = patientService.getPatientIdentifierType(2);
        PatientIdentifierDataDefinition pidd = new PatientIdentifierDataDefinition();
        pidd.setIncludeFirstNonNullOnly(true);
        pidd.addType(pit);

        PatientToObsDataDefinition d = new PatientToObsDataDefinition(pidd);
        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseObs(new ObsIdSet());
        EvaluatedObsData od = Context.getService(ObsDataService.class).evaluate(d, context);

        assertThat(od.getData().size(), is(0));
    }

    @Test
    public void evaluate_shouldProperlyPassParametersThroughToNestedDefinition() throws Exception {

        PatientToObsDataDefinition dataDef = new PatientToObsDataDefinition();

        PatientIdentifierDataDefinition pidd = new PatientIdentifierDataDefinition();
        pidd.setIncludeFirstNonNullOnly(true);
        pidd.addParameter(new Parameter("types", "Types", PatientIdentifierType.class, List.class, null, null));

        dataDef.setJoinedDefinition(pidd);

        ObsEvaluationContext context = new ObsEvaluationContext();
        PatientIdentifierType pit = patientService.getPatientIdentifierType(1);
        context.addParameterValue("types", Arrays.asList(pit));

        context.setBaseObs(new ObsIdSet(6));

        ObsData data = obsDataService.evaluate(dataDef, context);

        PatientIdentifier id1 = (PatientIdentifier) data.getData().get(6);
        Assert.assertEquals("6TS-4", id1.getIdentifier());
    }
}
