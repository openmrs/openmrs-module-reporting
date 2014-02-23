package org.openmrs.module.reporting.query.encounter.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.MappedParametersCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.MappedParametersEncounterQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class MappedParametersEncounterQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    @Test
    public void testEvaluate() throws Exception {
        Date date = DateUtil.parseDate("2008-08-01", "yyyy-MM-dd");
        BasicEncounterQuery original = new BasicEncounterQuery();
        original.addParameter(new Parameter("onOrAfter", "On Or After", Date.class));
        original.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));

        Map<String, String> renamedParameters = new HashMap<String, String>();
        renamedParameters.put("onOrAfter", "date");
        renamedParameters.put("onOrBefore", "date+1m");
        MappedParametersEncounterQuery renamed = new MappedParametersEncounterQuery(original, renamedParameters);

        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("date", date);
        EncounterQueryResult result = Context.getService(EncounterQueryService.class).evaluate(renamed, context);

        assertThat(result.getSize(), is(3));
        assertTrue(result.contains(3) && result.contains(4) && result.contains(5));
    }

}
