package org.openmrs.module.reporting.data.encounter.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.ObsForEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.ObsOnSameDateEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ObsOnSameDateEncounterDataEvaluatorTest extends BaseModuleContextSensitiveTest {

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
    public void testEvaluateForObsOnSameEncounter() throws Exception {

        Concept weight = conceptService.getConcept(5089);
        Concept cd4 = conceptService.getConcept(5497);

        // create an obs with a few members
        Patient patient = data.randomPatient().save();
        Encounter enc1 = data.randomEncounter().patient(patient).save();
        Obs obs1 = data.obs().concept(weight).value(60).encounter(enc1).save();
        Obs obs2 = data.obs().concept(cd4).value(350).encounter(enc1).save();

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(enc1.getId()));

        ObsOnSameDateEncounterDataDefinition def = new ObsOnSameDateEncounterDataDefinition();
        def.setQuestion(weight);
        def.setSingleObs(true);
        EvaluatedEncounterData results = encounterDataService.evaluate(def, context);

        assertThat(results.getData().size(), is(1));
        assertThat((Obs) results.getData().get(enc1.getId()), is(obs1));
    }

    @Test
    public void testEvaluateForObsOnDifferentEncounters() throws Exception {

        Concept weight = conceptService.getConcept(5089);
        Concept cd4 = conceptService.getConcept(5497);

        EncounterType vitalsEncounter = new EncounterType("VITALS", "Vitals encounter type");
        EncounterType artEncounter = new EncounterType("ART_FOLLOWUP", "ART visit encounter");
        encounterService.saveEncounterType(vitalsEncounter);
        encounterService.saveEncounterType(artEncounter);

        // create an obs with a few members
        Patient patient = data.randomPatient().save();
        Encounter enc1 = data.randomEncounter().patient(patient).save();
        enc1.setEncounterType(vitalsEncounter);
        enc1.setEncounterDatetime(DateUtil.getDateTime(2017, 10, 1, 9, 30, 0, 0));
        Obs obs1 = data.obs().concept(weight).value(60).encounter(enc1).save();


        Encounter enc2 = data.randomEncounter().patient(patient).save();
        enc2.setEncounterType(artEncounter);
        enc2.setEncounterDatetime(DateUtil.getDateTime(2017, 10, 1, 10, 30, 0, 0));
        Obs obs2 = data.obs().concept(cd4).value(350).encounter(enc2).save();

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(enc2.getId()));

        ObsOnSameDateEncounterDataDefinition def = new ObsOnSameDateEncounterDataDefinition();
        def.setQuestion(weight);
        def.setSingleObs(true);
        EvaluatedEncounterData results = encounterDataService.evaluate(def, context);

        assertThat(results.getData().size(), is(1));
        // it should return the Weight obs captured on the same date but in different encounter than the context encounter
        assertThat((Obs) results.getData().get(enc2.getId()), is(obs1));
    }

    @Test
    public void testEvaluateForObsWithAnswer() throws Exception {

        Concept civilStatus = conceptService.getConcept(4);
        Concept single = conceptService.getConcept(5);
        Concept married = conceptService.getConcept(6);

        // create an obs with a few members
        Patient patient = data.randomPatient().save();
        Encounter enc1 = data.randomEncounter().patient(patient).save();
        Encounter enc2 = data.randomEncounter().encounterDatetime(enc1.getEncounterDatetime()).patient(patient).save();
        Obs obs2 = data.obs().concept(civilStatus).value(married).encounter(enc2).save();

        EncounterEvaluationContext context = new EncounterEvaluationContext();
        context.setBaseEncounters(new EncounterIdSet(enc1.getId()));

        // First show that a normal ObsForEncounterDataDefinition would not include the obs from different encounter on same date
        ObsForEncounterDataDefinition def = new ObsForEncounterDataDefinition();
        def.setQuestion(civilStatus);
        def.setSingleObs(false);
        def.addAnswer(married);

        EvaluatedEncounterData results = encounterDataService.evaluate(def, context);
        assertThat(results.getData().size(), is(0));

        def = new ObsOnSameDateEncounterDataDefinition();
        def.setQuestion(civilStatus);
        def.setSingleObs(false);
        def.addAnswer(married);

        results = encounterDataService.evaluate(def, context);
        assertThat(results.getData().size(), is(1));
        List<Obs> obsList = (List<Obs>)results.getData().get(enc1.getId());
        assertThat(obsList.size(), is(1));
        assertThat(obsList, contains(obs2));
    }

}
