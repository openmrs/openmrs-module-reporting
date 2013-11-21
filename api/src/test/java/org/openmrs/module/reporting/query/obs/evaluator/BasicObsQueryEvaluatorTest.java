package org.openmrs.module.reporting.query.obs.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.ConceptService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ReportingMatchers;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.obs.ObsQueryResult;
import org.openmrs.module.reporting.query.obs.definition.BasicObsQuery;
import org.openmrs.module.reporting.query.obs.service.ObsQueryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.Assert.assertThat;

/**
 *
 */
public class BasicObsQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    ObsQueryService obsQueryService;

    @Autowired @Qualifier("conceptService")
    ConceptService conceptService;

    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    @Test
    public void testEvaluate() throws Exception {
        BasicObsQuery query = new BasicObsQuery();
        query.setOnOrAfter(DateUtil.parseDate("2008-08-15", "yyyy-MM-dd"));
        query.setOnOrBefore(DateUtil.parseDate("2008-08-15", "yyyy-MM-dd"));
        query.addConcept(conceptService.getConcept(5089));

        ObsQueryResult result = obsQueryService.evaluate(query, new EvaluationContext());
        assertThat(result, ReportingMatchers.hasExactlyIds(10));
    }

}
