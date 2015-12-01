package org.openmrs.module.reporting.data.visit.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.definition.PersonToVisitDataDefinition;
import org.openmrs.module.reporting.data.visit.service.VisitDataService;
import org.openmrs.module.reporting.evaluation.context.VisitEvaluationContext;
import org.openmrs.module.reporting.query.visit.VisitIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PersonToVisitDataEvaluatorTest extends BaseModuleContextSensitiveTest {

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

        PersonToVisitDataDefinition d = new PersonToVisitDataDefinition(new BirthdateDataDefinition());

        VisitEvaluationContext context = new VisitEvaluationContext();
        context.setBaseVisits(new VisitIdSet(3, 4));
        EvaluatedVisitData ed = Context.getService(VisitDataService.class).evaluate(d, context);

        Assert.assertEquals(2, ed.getData().size());
        BirthdateConverter c = new BirthdateConverter("yyyy-MM-dd");
        Assert.assertEquals("1975-04-08", c.convert(ed.getData().get(3)));
        Assert.assertEquals("2007-05-27", c.convert(ed.getData().get(4)));


    }

    @Test
    public void evaluate_shouldReturnEmptySetIfInputSetEmpty() throws Exception {

        PersonToVisitDataDefinition d = new PersonToVisitDataDefinition(new BirthdateDataDefinition());

        VisitEvaluationContext context = new VisitEvaluationContext();
        context.setBaseVisits(new VisitIdSet());
        EvaluatedVisitData ed = Context.getService(VisitDataService.class).evaluate(d, context);

        Assert.assertEquals(0, ed.getData().size());
    }


}
