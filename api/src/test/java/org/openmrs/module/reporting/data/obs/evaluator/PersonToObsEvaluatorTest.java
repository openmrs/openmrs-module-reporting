package org.openmrs.module.reporting.data.obs.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.definition.PersonToObsDataDefinition;
import org.openmrs.module.reporting.data.obs.service.ObsDataService;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.query.obs.ObsIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PersonToObsEvaluatorTest extends BaseModuleContextSensitiveTest {

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
    public void evaluate_shouldReturnPersonDataByForEachObsInContext() throws Exception {
        PersonToObsDataDefinition d = new PersonToObsDataDefinition(new BirthdateDataDefinition());

        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseObs(new ObsIdSet(20, 27));
        EvaluatedObsData ed = Context.getService(ObsDataService.class).evaluate(d, context);

        Assert.assertEquals(2, ed.getData().size());
        BirthdateConverter c = new BirthdateConverter("yyyy-MM-dd");
        Assert.assertEquals("1959-06-08", c.convert(ed.getData().get(20)));
        Assert.assertEquals("1997-07-08", c.convert(ed.getData().get(27)));

    }

    @Test
    public void evaluate_shouldEmptySetIfObsSetEmtpy() throws Exception {
        PersonToObsDataDefinition d = new PersonToObsDataDefinition(new BirthdateDataDefinition());

        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseObs(new ObsIdSet());
        EvaluatedObsData ed = Context.getService(ObsDataService.class).evaluate(d, context);

        Assert.assertEquals(0, ed.getData().size());
    }
}
