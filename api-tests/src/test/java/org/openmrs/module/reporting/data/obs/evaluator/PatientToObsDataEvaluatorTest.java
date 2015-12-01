package org.openmrs.module.reporting.data.obs.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.definition.PatientToObsDataDefinition;
import org.openmrs.module.reporting.data.obs.service.ObsDataService;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.query.obs.ObsIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PatientToObsDataEvaluatorTest extends BaseModuleContextSensitiveTest {

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
    public void evaluate_shouldReturnPatientDataForEachObsInThePassedContext() throws Exception {

        PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierType(2);
        PatientIdentifierDataDefinition pidd = new PatientIdentifierDataDefinition();
        pidd.setIncludeFirstNonNullOnly(true);
        pidd.addType(pit);
        
        PatientToObsDataDefinition d = new PatientToObsDataDefinition(pidd);
        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseObs(new ObsIdSet(20, 26));
        EvaluatedObsData od = Context.getService(ObsDataService.class).evaluate(d, context);

        assertThat(od.getData().size(), is(2));
        assertThat((PatientIdentifier) od.getData().get(20), is(Context.getPatientService().getPatient(21).getPatientIdentifier(pit)));
        assertThat((PatientIdentifier) od.getData().get(26), is(Context.getPatientService().getPatient(22).getPatientIdentifier(pit)));

    }

    @Test
    public void evaluate_shouldReturnEmptySetIfInputObsIdSetIsEmpty() throws Exception {

        PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierType(2);
        PatientIdentifierDataDefinition pidd = new PatientIdentifierDataDefinition();
        pidd.setIncludeFirstNonNullOnly(true);
        pidd.addType(pit);

        PatientToObsDataDefinition d = new PatientToObsDataDefinition(pidd);
        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseObs(new ObsIdSet());
        EvaluatedObsData od = Context.getService(ObsDataService.class).evaluate(d, context);

        assertThat(od.getData().size(), is(0));
    }

}
