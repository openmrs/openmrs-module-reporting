package org.openmrs.module.reporting.query.obs.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.module.reporting.common.ReportingMatchers;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.query.obs.ObsIdSet;
import org.openmrs.module.reporting.query.obs.ObsQueryResult;
import org.openmrs.module.reporting.query.obs.definition.AllObsQuery;
import org.openmrs.module.reporting.query.obs.service.ObsQueryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class AllObsQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    ObsQueryService obsQueryService;

    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    @Test
    public void testEvaluate() throws Exception {
        Cohort baseCohort = new Cohort();
        baseCohort.addMember(20);

        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(baseCohort);

        AllObsQuery query = new AllObsQuery();

        ObsQueryResult result = obsQueryService.evaluate(query, context);
        assertThat(result, ReportingMatchers.hasExactlyIds(17, 18, 19));
    }

    @Test
    public void testEvaluateInObsContext() throws Exception {

        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseObs(new ObsIdSet(7,9));

        AllObsQuery query = new AllObsQuery();

        ObsQueryResult result = obsQueryService.evaluate(query, context);
        assertThat(result, ReportingMatchers.hasExactlyIds(7,9));
    }

    @Test
    public void voidTestEvaluateInPatientAndObsContext() throws Exception {

        Cohort baseCohort = new Cohort();
        baseCohort.addMember(20);

        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseCohort(baseCohort);
        context.setBaseObs(new ObsIdSet(7,18));

        AllObsQuery query = new AllObsQuery();

        ObsQueryResult result = obsQueryService.evaluate(query, context);
        assertThat(result, ReportingMatchers.hasExactlyIds(18));

    }

    @Test
    public void testEvaluateWithEmptyCohort() throws Exception {
        Cohort baseCohort = new Cohort();

        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(baseCohort);

        AllObsQuery query = new AllObsQuery();

        ObsQueryResult result = obsQueryService.evaluate(query, context);
        assertThat(result.getSize(), is(0));
    }


    @Test
    public void testEvaluateInObsContextWithEmptySet() throws Exception {

        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseObs(new ObsIdSet());

        AllObsQuery query = new AllObsQuery();

        ObsQueryResult result = obsQueryService.evaluate(query, context);
        assertThat(result.getSize(), is(0));
    }

}
