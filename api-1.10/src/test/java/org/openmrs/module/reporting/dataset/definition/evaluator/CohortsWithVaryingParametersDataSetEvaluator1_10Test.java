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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.reporting.common.ReportingMatchers.isCohortWithExactlyIds;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.CohortsWithVaryingParametersDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

/**
 *
 */
public class CohortsWithVaryingParametersDataSetEvaluator1_10Test extends CohortsWithVaryingParametersDataSetEvaluatorTest {
	
	protected static final String XML_1_10_DATASET_PATH = "org/openmrs/module/reporting/include/ReportTestDataset-openmrs-1.10.xml";
	
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link org.openmrs.test.BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_1_10_DATASET_PATH);
	}
	
	@Test
	public void testEvaluate() throws Exception {
		EncounterCohortDefinition cd = new EncounterCohortDefinition();
		cd.setName("Has Encounter");
		cd.addParameter(new Parameter("locationList", "Location", Location.class));
		
		CohortsWithVaryingParametersDataSetDefinition dsd = new CohortsWithVaryingParametersDataSetDefinition();
		dsd.addColumn(cd);
		dsd.setRowLabelTemplate("At {{ locationList.name }}");
		
		for (Location location : locationService.getAllLocations()) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("locationList", location);
			dsd.addVaryingParameters(params);
		}
		
		DataSet result = dsdService.evaluate(dsd, new EvaluationContext());
		List<DataSetColumn> columns = result.getMetaData().getColumns();
		assertCollection(columns, columnMatching("rowLabel"), columnMatching("Has Encounter"));
		Iterator<DataSetRow> rowIterator = result.iterator();
		DataSetRow row = rowIterator.next();
		assertThat((String) row.getColumnValue("rowLabel"), is("At Unknown Location"));
		assertThat((Cohort) row.getColumnValue("Has Encounter"), isCohortWithExactlyIds(7));
		row = rowIterator.next();
		assertThat((String) row.getColumnValue("rowLabel"), is("At Xanadu"));
		assertThat((Cohort) row.getColumnValue("Has Encounter"), isCohortWithExactlyIds(7, 20, 21, 22, 23, 24));
		row = rowIterator.next();
		assertThat((String) row.getColumnValue("rowLabel"), is("At Never Never Land"));
		assertThat((Cohort) row.getColumnValue("Has Encounter"), isCohortWithExactlyIds());
	}
	
}
