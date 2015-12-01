package org.openmrs.module.reporting.query.obs.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.contrib.testdata.TestDataManager;
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

public class BasicObsQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    ObsQueryService obsQueryService;

    @Autowired @Qualifier("conceptService")
    ConceptService conceptService;

    @Autowired
    TestDataManager data;

    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    @Test
    public void testEvaluate() throws Exception {

        Concept someConcept = conceptService.getConcept(5089);

        Patient patient = data.randomPatient().save();
        Encounter enc1 = data.randomEncounter().patient(patient).encounterDatetime("2013-08-09 10:10:10").save();
        Obs beforeTimePeriod = data.obs().person(patient).concept(someConcept).encounter(enc1).value(10).save();

        Encounter enc2 = data.randomEncounter().patient(patient).encounterDatetime("2013-8-10 10:10:10").save();
        Obs firstDayOfTimePeriod = data.obs().person(patient).concept(someConcept).encounter(enc2).value(10).save();

        Encounter enc3 = data.randomEncounter().patient(patient).encounterDatetime("2013-8-11 10:10:10").save();
         Obs middleOfTimePeriod = data.obs().person(patient).concept(someConcept).encounter(enc3).value(10).save();

        Encounter enc4 = data.randomEncounter().patient(patient).encounterDatetime("2013-8-15 10:10:10").save();
        Obs lastDayOfTimePeriod = data.obs().person(patient).concept(someConcept).encounter(enc4).value(10).save();

        Encounter enc5 = data.randomEncounter().patient(patient).encounterDatetime("2013-8-17 10:10:10").save();
        Obs afterTimePeriod = data.obs().person(patient).concept(someConcept).encounter(enc5).value(10).save();

        BasicObsQuery query = new BasicObsQuery();
        query.setOnOrAfter(DateUtil.parseDate("2013-08-10", "yyyy-MM-dd"));
        query.setOnOrBefore(DateUtil.parseDate("2013-08-15", "yyyy-MM-dd"));
        query.addConcept(someConcept);

        ObsQueryResult result = obsQueryService.evaluate(query, new EvaluationContext());
        assertThat(result, ReportingMatchers.hasExactlyIds(firstDayOfTimePeriod.getId(), middleOfTimePeriod.getId(),
                lastDayOfTimePeriod.getId()));
    }

}
