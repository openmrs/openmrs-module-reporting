package org.openmrs.module.reporting.data.encounter.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterLocationDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EncounterLocationDataEvaluatorTest extends BaseModuleContextSensitiveTest {

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
    public void evaluate_shouldReturnEncounterLocationsGivenAnEncounterEvaluationContext() throws Exception {

        EncounterLocationDataDefinition d = new EncounterLocationDataDefinition();
        EncounterEvaluationContext encounterEvaluationContext = new EncounterEvaluationContext();
        encounterEvaluationContext.setBaseEncounters(new EncounterIdSet(4, 5));

        EvaluatedEncounterData ed = Context.getService(EncounterDataService.class).evaluate(d, encounterEvaluationContext);
        assertThat(ed.getData().size(), is(2));
        assertThat(ed.getData().get(4).toString(), is("Unknown Location"));
        assertThat(ed.getData().get(5).toString(), is("Xanadu"));
    }

    @Test
    public void evaluate_shouldReturnEncounterLocationsGivenAPatientEvaluationContext() throws Exception {

        EncounterLocationDataDefinition d = new EncounterLocationDataDefinition();
        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort("7,20"));

        EvaluatedEncounterData ed = Context.getService(EncounterDataService.class).evaluate(d, context);
        assertThat(ed.getData().size(), is(4));
        assertThat(ed.getData().get(3).toString(), is("Unknown Location"));
        assertThat(ed.getData().get(4).toString(), is("Unknown Location"));
        assertThat(ed.getData().get(5).toString(), is("Xanadu"));
        assertThat(ed.getData().get(6).toString(), is("Xanadu"));
    }

    @Test
    public void evaluate_shouldReturnEmptySetIfInputSetIsEmpty() throws Exception {

        EncounterLocationDataDefinition d = new EncounterLocationDataDefinition();
        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort());

        EvaluatedEncounterData ed = Context.getService(EncounterDataService.class).evaluate(d, context);
        assertThat(ed.getData().size(), is(0));
    }

}
