package org.openmrs.module.reporting.data.encounter.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.ObsForEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ObsForEncounterEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    private TestDataManager data;
    
    @Autowired
    @Qualifier("conceptService")
    private ConceptService conceptService;
    
    @Autowired
    @Qualifier("encounterService")
    private EncounterService encounterService;

    @Autowired
    private EncounterDataService encounterDataService;
    
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
    public void testEvaluateForSingleObs() throws Exception {

        Concept weight = conceptService.getConcept(5089);
        Concept cd4 = conceptService.getConcept(5497);

        // create an obs with a few members
        Patient patient = data.randomPatient().save();
        Encounter enc1 = data.randomEncounter().patient(patient).save();
        Obs obs1 = data.obs().concept(weight).value(60).encounter(enc1).save();
        Obs obs2 = data.obs().concept(cd4).value(350).encounter(enc1).save();

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(enc1.getId()));

        ObsForEncounterDataDefinition def = new ObsForEncounterDataDefinition();
        def.setQuestion(weight);
        def.setSingleObs(true);
        EvaluatedEncounterData results = encounterDataService.evaluate(def, context);

        assertThat(results.getData().size(), is(1));
        assertThat((Obs) results.getData().get(enc1.getId()), is(obs1));

    }

    @Test
    public void testEvaluateForMultipleObs() throws Exception {

        Concept weight = conceptService.getConcept(5089);

        // create an obs with a few members
        Patient patient = data.randomPatient().save();
        Encounter enc1 = data.randomEncounter().patient(patient).save();
        Obs obs1 = data.obs().concept(weight).value(60).encounter(enc1).save();
        Obs obs2 = data.obs().concept(weight).value(62).encounter(enc1).save();

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(enc1.getId()));

        ObsForEncounterDataDefinition def = new ObsForEncounterDataDefinition();
        def.setQuestion(weight);
        def.setSingleObs(false);
        EvaluatedEncounterData results = encounterDataService.evaluate(def, context);

        assertThat(results.getData().size(), is(1));
        assertThat(((List<Obs>) results.getData().get(enc1.getId())).size(), is(2));
        assertThat(((List<Obs>) results.getData().get(enc1.getId())),
                containsInAnyOrder(obs1, obs2));

    }

    @Test
    public void testMakeSureEmptySingleEntryEvenIfNoMatchingObsInGroup() throws Exception {

        Concept weight = conceptService.getConcept(5089);
        Concept cd4 = conceptService.getConcept(5497);

        // create an obs with a few members
        Patient patient = data.randomPatient().save();
        Encounter enc1 = data.randomEncounter().patient(patient).save();
        Obs obs1 = data.obs().concept(weight).value(60).encounter(enc1).save();

        // add another encounter with no obs
        Encounter enc2 = data.randomEncounter().patient(patient).save();

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(enc1.getId(), enc2.getId()));

        ObsForEncounterDataDefinition def = new ObsForEncounterDataDefinition();
        def.setQuestion(weight);
        def.setSingleObs(true);
        EvaluatedEncounterData results = encounterDataService.evaluate(def, context);

        assertThat((Obs) results.getData().get(enc1.getId()), is(obs1));
        assertNull(results.getData().get(enc2.getId()));
    }


    @Test
    public void testMakeSureEmptyListEntryEvenIfNoMatchingObsInGroup() throws Exception {

        Concept weight = conceptService.getConcept(5089);

        // create an obs with a few members
        Patient patient = data.randomPatient().save();
        Encounter enc1 = data.randomEncounter().patient(patient).save();
        Obs obs1 = data.obs().concept(weight).value(60).encounter(enc1).save();
        Obs obs2 = data.obs().concept(weight).value(60).encounter(enc1).save();

        // add another encounter with no obs
        Encounter enc2 = data.randomEncounter().patient(patient).save();

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(enc1.getId(), enc2.getId()));

        ObsForEncounterDataDefinition def = new ObsForEncounterDataDefinition();
        def.setQuestion(weight);
        def.setSingleObs(false);
        EvaluatedEncounterData results = encounterDataService.evaluate(def, context);

        assertThat(((List<Obs>) results.getData().get(enc1.getId())).size(), is(2));
        assertThat(((List<Obs>) results.getData().get(enc1.getId())), containsInAnyOrder(obs1, obs2));
        assertNull(results.getData().get(enc2.getId()));
    }

    @Test
    public void testEvaluateForSingleObsWhenInPatientContext() throws Exception {

        Concept weight = conceptService.getConcept(5089);
        Concept cd4 = conceptService.getConcept(5497);

        // create an obs with a few members
        Patient patient = data.randomPatient().save();
        Encounter enc1 = data.randomEncounter().patient(patient).save();
        Obs obs1 = data.obs().concept(weight).value(60).encounter(enc1).save();
        Obs obs2 = data.obs().concept(cd4).value(350).encounter(enc1).save();

        // set a cohort, not a set of encounter ids
        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort(patient.getId().toString()));

        ObsForEncounterDataDefinition def = new ObsForEncounterDataDefinition();
        def.setQuestion(weight);
        def.setSingleObs(true);
        EvaluatedEncounterData results = encounterDataService.evaluate(def, context);

        assertThat(results.getData().size(), is(1));
        assertThat((Obs) results.getData().get(enc1.getId()), is(obs1));

    }

    @Test
    public void testShouldReturnEmptySetWhenInputSetIsEmpty() throws Exception {

        Concept weight = conceptService.getConcept(5089);

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet());

        ObsForEncounterDataDefinition def = new ObsForEncounterDataDefinition();
        def.setQuestion(weight);
        def.setSingleObs(true);
        EvaluatedEncounterData results = encounterDataService.evaluate(def, context);

        assertThat(results.getData().size(), is(0));

    }


}
