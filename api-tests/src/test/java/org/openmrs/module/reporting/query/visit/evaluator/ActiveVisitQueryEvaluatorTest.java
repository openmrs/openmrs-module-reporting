package org.openmrs.module.reporting.query.visit.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.context.VisitEvaluationContext;
import org.openmrs.module.reporting.query.visit.VisitQueryResult;
import org.openmrs.module.reporting.query.visit.definition.ActiveVisitQuery;
import org.openmrs.module.reporting.query.visit.service.VisitQueryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class ActiveVisitQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    private VisitQueryService service;

    @Autowired
    private VisitService visitService;

    @Autowired
    private TestDataManager data;

    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    @Test
    public void testEvaluate() throws Exception {
        Date someTimeYesterday = DateUtil.adjustDate(new Date(), -1, DurationUnit.DAYS);
        Date startOfYesterday = DateUtil.getStartOfDay(someTimeYesterday);
        Date endOfYesterday = DateUtil.getEndOfDay(someTimeYesterday);

        // there are some active visits in the dataset already
        List<Integer> activeVisits = new ArrayList<Integer>();
        activeVisits.add(1);
        activeVisits.add(2);
        activeVisits.add(3);
        activeVisits.add(4);
        activeVisits.add(5);

        // now we will create a couple inactive visits, and two active ones
        Patient patient1 = data.randomPatient().save();
        Patient patient2 = data.randomPatient().save();
        data.visit().patient(patient1).visitType(1).location(1).started("2013-04-05").stopped("2013-04-06").save();
        data.visit().patient(patient2).visitType(1).location(1).started("2013-04-05").stopped("2013-04-06").save();
        Visit active1 = data.visit().patient(patient1).visitType(1).location(1).started(startOfYesterday).stopped(endOfYesterday).save();
        Visit active2 = data.visit().patient(patient2).visitType(1).location(1).started(startOfYesterday).save();
        activeVisits.add(active1.getId());
        activeVisits.add(active2.getId());

        ActiveVisitQuery query = new ActiveVisitQuery();
        query.setAsOfDate(someTimeYesterday);

        VisitQueryResult result = service.evaluate(query, new VisitEvaluationContext());
        assertThat(result.getMemberIds(), containsInAnyOrder(activeVisits.toArray()));
    }

}