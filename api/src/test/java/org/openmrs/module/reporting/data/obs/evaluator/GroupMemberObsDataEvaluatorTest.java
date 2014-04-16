package org.openmrs.module.reporting.data.obs.evaluator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.definition.GroupMemberObsDataDefinition;
import org.openmrs.module.reporting.data.obs.service.ObsDataService;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.query.obs.ObsIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GroupMemberObsDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    ObsDataService obsDataService;

    @Autowired
    TestDataManager data;
    
    @Autowired
    @Qualifier("conceptService")
    ConceptService conceptService;
    
    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    @Test
    public void testEvaluateForSingleObs() throws Exception {

        Concept weight = conceptService.getConcept(5089);
        Concept cd4 = conceptService.getConcept(5497);
        Concept groupConcept = conceptService.getConcept(10001);
        
        // create an obs with a few members
        Patient patient = data.randomPatient().save();
        Encounter enc = data.randomEncounter().patient(patient).save();
        Obs obsMember1 = data.obs().concept(weight).value(60).encounter(enc).save();
        Obs obsMember2 = data.obs().concept(cd4).value(350).encounter(enc).save();
        Obs obsGroup = data.obs().concept(groupConcept).encounter(enc)
                .member(obsMember1).member(obsMember2).save();


        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseObs(new ObsIdSet(obsGroup.getId()));

        GroupMemberObsDataDefinition def = new GroupMemberObsDataDefinition();
        def.setQuestion(weight);
        def.setSingleObs(true);
        EvaluatedObsData results = obsDataService.evaluate(def, context);

        assertThat(results.getData().size(), is(1));
        assertThat((Obs) results.getData().get(obsGroup.getId()), is(obsMember1));

    }

    @Test
    public void testEvaluateForMultipleObs() throws Exception {

        Concept weight = conceptService.getConcept(5089);
        Concept groupConcept = conceptService.getConcept(10001);

        // create an obs with a few members
        Patient patient = data.randomPatient().save();
        Encounter enc = data.randomEncounter().patient(patient).save();
        Obs obsMember1 = data.obs().concept(weight).value(60).encounter(enc).save();
        Obs obsMember2 = data.obs().concept(weight).value(62).encounter(enc).save();
        Obs obsGroup = data.obs().concept(groupConcept).encounter(enc)
                .member(obsMember1).member(obsMember2).save();


        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseObs(new ObsIdSet(obsGroup.getId()));

        GroupMemberObsDataDefinition def = new GroupMemberObsDataDefinition();
        def.setQuestion(weight);
        def.setSingleObs(false);
        EvaluatedObsData results = obsDataService.evaluate(def, context);

        assertThat(results.getData().size(), is(1));
        assertThat(((List<Obs>) results.getData().get(obsGroup.getId())).size(), is(2));
        assertThat(((List<Obs>) results.getData().get(obsGroup.getId())),
                containsInAnyOrder(obsMember1, obsMember2));

    }

    @Test
	@Ignore // Ignoring this test for now, since in 1.9 the ObsValidator doesn't allow empty obs groups to be saved
    public void testMakeSureEmptySingleEntryEvenIfNoMatchingObsInGroup() throws Exception {

        Concept groupConcept = conceptService.getConcept(10001);
        Concept weight = conceptService.getConcept(5089);

        // create an obs group with no members
        Patient patient = data.randomPatient().save();
        Encounter enc = data.randomEncounter().patient(patient).save();
        Obs obsGroup = data.obs().concept(groupConcept).encounter(enc).save();

        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseObs(new ObsIdSet(obsGroup.getId()));

        GroupMemberObsDataDefinition def = new GroupMemberObsDataDefinition();
        def.setQuestion(weight);
        def.setSingleObs(true);  // single obs format
        EvaluatedObsData results = obsDataService.evaluate(def, context);

        assertThat(results.getData().size(), is(1));
        assertThat(results.getData().get(obsGroup.getId()), nullValue());

    }

    @Test
	@Ignore // Ignoring this test for now, since in 1.9 the ObsValidator doesn't allow empty obs groups to be saved
	public void testMakeSureEmptyListEntryEvenIfNoMatchingObsInGroup() throws Exception {

        Concept groupConcept = conceptService.getConcept(10001);
        Concept weight = conceptService.getConcept(5089);

        // create an obs group with no members
        Patient patient = data.randomPatient().save();
        Encounter enc = data.randomEncounter().patient(patient).save();
        Obs obsGroup = data.obs().concept(groupConcept).encounter(enc).save();

        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseObs(new ObsIdSet(obsGroup.getId()));

        GroupMemberObsDataDefinition def = new GroupMemberObsDataDefinition();
        def.setQuestion(weight);
        def.setSingleObs(false);  // not single obs format
        EvaluatedObsData results = obsDataService.evaluate(def, context);

        assertThat(results.getData().size(), is(1));
        assertThat(results.getData().get(obsGroup.getId()), instanceOf(List.class));
        assertThat(((List<Obs>) results.getData().get(obsGroup.getId())).size(), is(0));

    }

    @Test
    public void testMakeSureWorksIfBaseObsContextIsEmptyList() throws Exception {

        Concept weight = conceptService.getConcept(5089);

        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseObs(new ObsIdSet());

        GroupMemberObsDataDefinition def = new GroupMemberObsDataDefinition();
        def.setQuestion(weight);
        EvaluatedObsData results = obsDataService.evaluate(def, context);

        assertThat(results.getData().size(), is(0));
    }
    
}
