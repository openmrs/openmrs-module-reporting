package org.openmrs.module.reporting.data.encounter.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterVisitDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EncounterVisitDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    protected static final String XML_ENCOUNTER_VISIT_TEST_DATASET = "EncounterVisitTestDataset.xml";

    /**
     * Run this before each unit test in this class. The "@Before" method in
     * {@link org.openmrs.test.BaseContextSensitiveTest} is run right before this method.
     *
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
        executeDataSet(XML_DATASET_PATH + XML_ENCOUNTER_VISIT_TEST_DATASET);
    }

    @Test
    public void evaluate_shouldReturnEncounterVisitsGivenAnEncounterEvaluationContext() throws Exception {

        EncounterVisitDataDefinition d = new EncounterVisitDataDefinition();
        EncounterEvaluationContext encounterEvaluationContext = new EncounterEvaluationContext();
        encounterEvaluationContext.setBaseEncounters(new EncounterIdSet(61, 62));

        EvaluatedEncounterData ed = Context.getService(EncounterDataService.class).evaluate(d, encounterEvaluationContext);
        assertThat(ed.getData().size(), is(2));
        assertThat(((Visit) ed.getData().get(61)).getId(), is(1));
        assertThat(((Visit) ed.getData().get(62)).getId(), is(2));
    }


}
