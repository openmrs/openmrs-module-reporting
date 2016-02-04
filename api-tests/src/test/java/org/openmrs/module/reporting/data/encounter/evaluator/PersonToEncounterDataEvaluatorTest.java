package org.openmrs.module.reporting.data.encounter.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.PersonToEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PersonToEncounterDataEvaluatorTest extends BaseModuleContextSensitiveTest {

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
    public void evaluate_shouldReturnPersonDataByForEachEncounterInContext() throws Exception {

        PersonToEncounterDataDefinition d = new PersonToEncounterDataDefinition(new BirthdateDataDefinition());

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(3,6));
        EvaluatedEncounterData ed = Context.getService(EncounterDataService.class).evaluate(d, context);

        Assert.assertEquals(2, ed.getData().size());
        BirthdateConverter c = new BirthdateConverter("yyyy-MM-dd");
        Assert.assertEquals("1976-08-25", c.convert(ed.getData().get(3)));
        Assert.assertEquals("1925-02-08", c.convert(ed.getData().get(6)));

    }

    @Test
    public void evaluate_shouldEmptySetIfInputSetEmpty() throws Exception {

        PersonToEncounterDataDefinition d = new PersonToEncounterDataDefinition(new BirthdateDataDefinition());

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet());
        EvaluatedEncounterData ed = Context.getService(EncounterDataService.class).evaluate(d, context);

        Assert.assertEquals(0, ed.getData().size());
    }
}
