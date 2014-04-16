/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.CohortsWithVaryingParametersDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.reporting.common.ReportingMatchers.isCohortWithExactlyIds;

/**
 *
 */
public class CohortsWithVaryingParametersDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    DataSetDefinitionService dsdService;

    @Autowired @Qualifier("locationService")
    LocationService locationService;

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
    public void testEvaluate() throws Exception {
        EncounterCohortDefinition cd = new EncounterCohortDefinition();
        cd.setName("Has Encounter");
        cd.addParameter(new Parameter("locationList", "Location", Location.class));

        CohortsWithVaryingParametersDataSetDefinition dsd = new CohortsWithVaryingParametersDataSetDefinition();
        dsd.addColumn(cd);
        dsd.setRowLabelTemplate("At {{ locationList.name }}");

		String[] locationNames = {"Never Never Land", "Unknown Location", "Xanadu"};
        for (String locationName : locationNames) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("locationList", locationService.getLocation(locationName));
            dsd.addVaryingParameters(params);
        }

        DataSet result = dsdService.evaluate(dsd, new EvaluationContext());
        List<DataSetColumn> columns = result.getMetaData().getColumns();
        assertCollection(columns, columnMatching("rowLabel"), columnMatching("Has Encounter"));
        Iterator<DataSetRow> rowIterator = result.iterator();
        DataSetRow row = rowIterator.next();
        assertThat((String) row.getColumnValue("rowLabel"), is("At Never Never Land"));
        assertThat((Cohort) row.getColumnValue("Has Encounter"), isCohortWithExactlyIds());
        row = rowIterator.next();
        assertThat((String) row.getColumnValue("rowLabel"), is("At Unknown Location"));
        assertThat((Cohort) row.getColumnValue("Has Encounter"), isCohortWithExactlyIds(7));
        row = rowIterator.next();
        assertThat((String) row.getColumnValue("rowLabel"), is("At Xanadu"));
        assertThat((Cohort) row.getColumnValue("Has Encounter"), isCohortWithExactlyIds(7, 20, 21, 22, 23, 24));
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
