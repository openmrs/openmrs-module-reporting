package org.openmrs.module.reporting.data.encounter.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.PatientToEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PatientToEncounterDataEvaluatorTest extends BaseModuleContextSensitiveTest {

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
    public void evaluate_shouldReturnPatientDataForEachEncounterInThePassedContext() throws Exception {

        PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierType(2);
        PatientIdentifierDataDefinition pidd = new PatientIdentifierDataDefinition();
        pidd.setIncludeFirstNonNullOnly(true);
        pidd.addType(pit);

        PatientToEncounterDataDefinition d = new PatientToEncounterDataDefinition(pidd);
        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(8,9,10));     // in our demo set include two encounters for same patient
        EvaluatedEncounterData ed = Context.getService(EncounterDataService.class).evaluate(d, context);
        
        assertThat(ed.getData().size(), is(3));
        assertThat((PatientIdentifier) ed.getData().get(8), is(Context.getPatientService().getPatient(21).getPatientIdentifier(pit)));
        assertThat((PatientIdentifier) ed.getData().get(9), is(Context.getPatientService().getPatient(22).getPatientIdentifier(pit)));
        assertThat((PatientIdentifier) ed.getData().get(10), is(Context.getPatientService().getPatient(22).getPatientIdentifier(pit)));

    }

    @Test
    public void evaluate_shouldReturnEmptySetIfInputSetEmpty() throws Exception {

        PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierType(2);
        PatientIdentifierDataDefinition pidd = new PatientIdentifierDataDefinition();
        pidd.setIncludeFirstNonNullOnly(true);
        pidd.addType(pit);

        PatientToEncounterDataDefinition d = new PatientToEncounterDataDefinition(pidd);
        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet());
        EvaluatedEncounterData ed = Context.getService(EncounterDataService.class).evaluate(d, context);

        assertThat(ed.getData().size(), is(0));
    }

}
