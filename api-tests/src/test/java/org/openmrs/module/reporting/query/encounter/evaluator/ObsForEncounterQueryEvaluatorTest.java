/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
