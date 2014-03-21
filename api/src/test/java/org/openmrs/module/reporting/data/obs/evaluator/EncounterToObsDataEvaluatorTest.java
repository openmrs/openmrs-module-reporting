package org.openmrs.module.reporting.data.obs.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.encounter.definition.EncounterIdDataDefinition;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.definition.EncounterToObsDataDefinition;
import org.openmrs.module.reporting.data.obs.service.ObsDataService;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.query.obs.ObsIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class EncounterToObsDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    private TestDataManager data;

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
    public void evaluate_shouldReturnEncounterDataForEachObsInThePassedContext() throws Exception {

        // add an "encounterless" obs to make sure that is handled correctly
        Patient patient = data.randomPatient().save();
        Obs obsWithoutEncounter =  data.obs().obsDatetime(new Date()).person(patient)
                .concept(Context.getConceptService().getConcept(5089))
                .location(Context.getLocationService().getLocation(1)).value(350)
                .save();

        EncounterToObsDataDefinition d = new EncounterToObsDataDefinition(new EncounterIdDataDefinition());

        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseObs(new ObsIdSet(20, 21, 26, obsWithoutEncounter.getId()));
        EvaluatedObsData od = Context.getService(ObsDataService.class).evaluate(d, context);

        assertThat(od.getData().size(), is(4));
        assertThat((Integer) od.getData().get(20), is(7));
        assertThat((Integer) od.getData().get(21), is(7));
        assertThat((Integer) od.getData().get(26), is(9));
        assertNull(od.getData().get(obsWithoutEncounter.getId()));

    }


    @Test
    public void evaluate_shouldReturnEmptySetIfObsIdSetIsEmpty() throws Exception {


        EncounterToObsDataDefinition d = new EncounterToObsDataDefinition(new EncounterIdDataDefinition());

        ObsEvaluationContext context = new ObsEvaluationContext();
        context.setBaseObs(new ObsIdSet());
        EvaluatedObsData od = Context.getService(ObsDataService.class).evaluate(d, context);

        assertThat(od.getData().size(), is(0));


    }
}
