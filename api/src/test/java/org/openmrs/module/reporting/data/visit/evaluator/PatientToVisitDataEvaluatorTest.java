package org.openmrs.module.reporting.data.visit.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.definition.PatientToVisitDataDefinition;
import org.openmrs.module.reporting.data.visit.service.VisitDataService;
import org.openmrs.module.reporting.evaluation.context.VisitEvaluationContext;
import org.openmrs.module.reporting.query.visit.VisitIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PatientToVisitDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

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

        PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierType(2);
        PatientIdentifierDataDefinition pidd = new PatientIdentifierDataDefinition();
        pidd.setIncludeFirstNonNullOnly(true);
        pidd.addType(pit);

        PatientToVisitDataDefinition d = new PatientToVisitDataDefinition(pidd);
        VisitEvaluationContext context = new VisitEvaluationContext();
        context.setBaseVisits(new VisitIdSet(1, 2, 4));     // in our demo set include two visits for same patient
        EvaluatedVisitData ed = Context.getService(VisitDataService.class).evaluate(d, context);

        assertThat(ed.getData().size(), is(3));
        assertThat((PatientIdentifier) ed.getData().get(1), is(Context.getPatientService().getPatient(2).getPatientIdentifier(pit)));
        assertThat((PatientIdentifier) ed.getData().get(2), is(Context.getPatientService().getPatient(2).getPatientIdentifier(pit)));
        assertThat((PatientIdentifier) ed.getData().get(4), is(Context.getPatientService().getPatient(6).getPatientIdentifier(pit)));

    }

    @Test
    public void evaluate_shouldReturnEmptySetIfInputSetEmpty() throws Exception {

        PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierType(2);
        PatientIdentifierDataDefinition pidd = new PatientIdentifierDataDefinition();
        pidd.setIncludeFirstNonNullOnly(true);
        pidd.addType(pit);

        PatientToVisitDataDefinition d = new PatientToVisitDataDefinition(pidd);
        VisitEvaluationContext context = new VisitEvaluationContext();
        context.setBaseVisits(new VisitIdSet());
        EvaluatedVisitData ed = Context.getService(VisitDataService.class).evaluate(d, context);

        assertThat(ed.getData().size(), is(0));
    }


}
