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

package org.openmrs.module.reporting.query.encounter.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.CodedObsForEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.NumericObsForEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.ObsForEncounterQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.junit.Assert.assertThat;
import static org.openmrs.module.reporting.common.ReportingMatchers.hasExactlyIds;

public class ObsForEncounterQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

    protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

    @Autowired
    EncounterQueryService encounterQueryService;

	@Autowired
	EncounterService encounterService;

	@Autowired
	ConceptService conceptService;

	@Autowired
	LocationService locationService;

    @Before
    public void setup() throws Exception {
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
    }

    @Test
    public void evaluate_shouldFilterByType() throws Exception {
        ObsForEncounterQuery query = new ObsForEncounterQuery();
		query.setQuestion(conceptService.getConcept(5089));
        query.addEncounterType(encounterService.getEncounterType(2));
		test(query, 3);
		query.addEncounterType(encounterService.getEncounterType(1));
		test(query, 3,4,5);
    }

	@Test
	public void evaluate_shouldFilterByEncounterDate() throws Exception {
		ObsForEncounterQuery query = new ObsForEncounterQuery();
		query.setQuestion(conceptService.getConcept(5089));
		query.setEncounterOnOrAfter(DateUtil.getDateTime(2008, 8, 2));
		test(query, 4,5,6,7,8,9,10);
		query.setEncounterOnOrBefore(DateUtil.getDateTime(2008, 8, 19));
		test(query, 4,5);
	}

	@Test
	public void evaluate_shouldFilterByEncounterLocation() throws Exception {
		ObsForEncounterQuery query = new ObsForEncounterQuery();
		query.setQuestion(conceptService.getConcept(5089));
		query.addEncounterLocation(locationService.getLocation(1));
		test(query, 3,4);
		query.setEncounterLocations(Arrays.asList(locationService.getLocation(2)));
		test(query, 5,6,7,8,9,10);
	}

	@Test
	public void evaluate_shouldFilterByMinValueInclusive() throws Exception {
		NumericObsForEncounterQuery query = new NumericObsForEncounterQuery();
		query.setQuestion(conceptService.getConcept(5089));
		query.setOperator1(RangeComparator.GREATER_EQUAL);
		query.setValue1(180.0);
		test(query, 6,9,10);
	}

	@Test
	public void evaluate_shouldFilterByMinValueExclusive() throws Exception {
		NumericObsForEncounterQuery query = new NumericObsForEncounterQuery();
		query.setQuestion(conceptService.getConcept(5089));
		query.setOperator1(RangeComparator.GREATER_THAN);
		query.setValue1(180.0);
		test(query, 10);
	}

	@Test
	public void evaluate_shouldFilterByMaxValueInclusive() throws Exception {
		NumericObsForEncounterQuery query = new NumericObsForEncounterQuery();
		query.setQuestion(conceptService.getConcept(5089));
		query.setOperator1(RangeComparator.LESS_EQUAL);
		query.setValue1(180.0);
		test(query, 9,8,7,6,5,4,3);
	}

	@Test
	public void evaluate_shouldFilterByMaxValueExclusive() throws Exception {
		NumericObsForEncounterQuery query = new NumericObsForEncounterQuery();
		query.setQuestion(conceptService.getConcept(5089));
		query.setOperator1(RangeComparator.LESS_THAN);
		query.setValue1(180.0);
		test(query, 8,7,5,4,3);
	}

	@Test
	public void evaluate_shouldFilterByCodedValuesToInclude() throws Exception {
		CodedObsForEncounterQuery query = new CodedObsForEncounterQuery();
		query.setQuestion(conceptService.getConcept(21));
		query.addConceptToInclude(conceptService.getConcept(8));
		test(query, 3);
		query.addConceptToInclude(conceptService.getConcept(7));
		test(query, 3,4);
	}

	protected void test(ObsForEncounterQuery query, Integer...expectedEncounterIds) throws Exception {
		EncounterQueryResult result = encounterQueryService.evaluate(query, new EvaluationContext());
		assertThat(result, hasExactlyIds(expectedEncounterIds));
	}
}
