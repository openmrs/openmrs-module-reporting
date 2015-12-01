package org.openmrs.module.reporting.data.obs.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.definition.ObsIdDataDefinition;
import org.openmrs.module.reporting.data.obs.service.ObsDataService;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.query.obs.ObsIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class ObsIdDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    ObsDataService obsDataService;

    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    @Test
    public void testEvaluate() throws Exception {
        ObsIdDataDefinition def = new ObsIdDataDefinition();

        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseObs(new ObsIdSet(6, 7, 9));

        EvaluatedObsData results = obsDataService.evaluate(def, context);

        assertThat((Integer) results.getData().get(6), is(6));
        assertThat((Integer) results.getData().get(7), is(7));
        assertThat((Integer) results.getData().get(9), is(9));
        assertThat(results.getData().get(100), nullValue());
    }

}
