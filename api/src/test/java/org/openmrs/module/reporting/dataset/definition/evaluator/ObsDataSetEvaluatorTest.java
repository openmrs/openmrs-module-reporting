package org.openmrs.module.reporting.dataset.definition.evaluator;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.obs.definition.ObsIdDataDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.ObsDataSetDefinition;
import org.openmrs.module.reporting.query.obs.definition.BasicObsQuery;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObsDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    private ObsDataSetEvaluator evaluator;

    @Autowired
    private TestDataManager data;
    
    @Autowired
    @Qualifier("conceptService")
    private ConceptService conceptService;

    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }


    @Test
    public void testBasicEvaluation() throws Exception {

        // pick a concept that has no existing obs in the test dataset
        Concept concept = conceptService.getConcept(10002);
		Concept valueCoded = conceptService.getConcept(792);
        
        Patient patient = data.randomPatient().save();
        Encounter enc = data.randomEncounter().patient(patient).save();
        Obs obs1 = data.obs().concept(concept).value(valueCoded).encounter(enc).save();
        Obs obs2 = data.obs().concept(concept).value(valueCoded).encounter(enc).save();

        ObsDataSetDefinition dsd = new ObsDataSetDefinition();

        BasicObsQuery query = new BasicObsQuery();
        query.addConcept(concept);
        dsd.addRowFilter(query, null);

        ObsIdDataDefinition definition = new ObsIdDataDefinition();
        dsd.addColumn("ids", definition, null, null);

        DataSet dataSet = evaluator.evaluate(dsd, null);

        List<Integer> results = new ArrayList<Integer>();
        Iterator<DataSetRow> i = dataSet.iterator();

        while (i.hasNext()) {
            results.add((Integer) i.next().getColumnValue("ids"));
        }

        assertThat(results.size(), is(2));
        assertThat(results, containsInAnyOrder(obs1.getId(), obs2.getId()));
        
    }


}
