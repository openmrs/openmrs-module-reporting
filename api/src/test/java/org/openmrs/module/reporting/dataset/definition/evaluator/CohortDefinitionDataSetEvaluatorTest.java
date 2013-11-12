package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.LocationService;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.CohortDefinitionDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.reporting.common.ReportingMatchers.isCohortWithExactlyIds;

/**
 *
 */
public class CohortDefinitionDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    private DataSetDefinitionService dsdService;

    @Autowired @Qualifier("locationService")
    private LocationService locationService;

    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    @Test
    public void testEvaluate() throws Exception {
        EncounterCohortDefinition cd1 = new EncounterCohortDefinition();
        cd1.addLocation(locationService.getLocation(1));

        EncounterCohortDefinition cd2 = new EncounterCohortDefinition();
        cd2.addLocation(locationService.getLocation(2));

        CohortDefinitionDataSetDefinition dsd = new CohortDefinitionDataSetDefinition();
        dsd.addColumn("at-1", "At 1", cd1);
        dsd.addColumn("at-2", "At 2", cd2);

        MapDataSet result = (MapDataSet) dsdService.evaluate(dsd, new EvaluationContext());
        List<DataSetColumn> columns = result.getMetaData().getColumns();
        assertCollection(columns, columnMatching("at-1"), columnMatching("at-2"));
        assertCollection(result.getData().getColumnValues().values(), isCohortWithExactlyIds(7), isCohortWithExactlyIds(7, 20, 21, 22, 23, 24));
    }

    private Matcher<DataSetColumn> columnMatching(final String name) {
        return new BaseMatcher<DataSetColumn>() {
            @Override
            public boolean matches(Object o) {
                DataSetColumn actual = (DataSetColumn) o;
                return name.equals(actual.getName());
            }

            @Override
            public void describeTo(Description description) {
                // TODO
            }
        };
    }

    /**
     * We can't use IsIterableContainingInOrder from Hamcrest because the OpenMRS 1.6.6 version of JUnit contains bad
     * versions of hamcrest classes
     * @param collection
     * @param matchers
     */
    private void assertCollection(Collection<?> collection, Matcher... matchers) {
        assertThat(collection.size(), is(matchers.length));
        List items = new ArrayList(collection);
        for (int i = 0; i < matchers.length; ++i) {
            assertThat(items.get(i), matchers[i]);
        }
    }
}
