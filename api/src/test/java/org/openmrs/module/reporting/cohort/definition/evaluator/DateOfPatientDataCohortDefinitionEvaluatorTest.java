/**
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
package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.DateOfPatientDataCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Date;

/**
 * This tests the evaluation of an DateOfPatientDataCohortDefinition
 * TestData:
 *
 * person_id	dates for obs: 5089
 * 7			2008-08-01, 2008-08-15, 2008-08-19
 * 20			2009-08-19
 * 21			2009-08-19, 2009-09-19
 * 22			2009-08-19, 2009-09-19
 *
 */
public class DateOfPatientDataCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	@Test
	public void evaluate_shouldReturnTheRightPatients() throws Exception {
		testPatients(DateUtil.getDateTime(2008, 8, 31), null, null, 2, DurationUnit.WEEKS, 7); // Most recent weight within the last 2 weeks
		testPatients(DateUtil.getDateTime(2009, 9, 30), null, null, 1, DurationUnit.MONTHS, 21, 22); // Most recent weight within the last month
		testPatients(DateUtil.getDateTime(2009, 9, 19), 0, DurationUnit.DAYS, 0, DurationUnit.DAYS, 21,22); // Most recent weight on effectiveDate
		testPatients(DateUtil.getDateTime(2009, 9, 30), 1, DurationUnit.YEARS, null, null, 7); // No weight within the last year
		testPatients(DateUtil.getDateTime(2009, 9, 30), 1, DurationUnit.DAYS, null, null, 7,20,21,22); // No weight within the last day
	}

	private void testPatients(Date effectiveDate, Integer min, DurationUnit minUnits, Integer max, DurationUnit maxUnits, Integer...expectedPatients) throws EvaluationException {
		ObsForPersonDataDefinition dd = new ObsForPersonDataDefinition();
		dd.setWhich(TimeQualifier.LAST);
		dd.setQuestion(Context.getConceptService().getConcept(5089));
		dd.setOnOrBefore(effectiveDate);

		DateOfPatientDataCohortDefinition cd = new DateOfPatientDataCohortDefinition();
		cd.setPatientDataDefinition(Mapped.mapStraightThrough(new PersonToPatientDataDefinition(dd)));
		cd.setDataConverter(new PropertyConverter(Obs.class, "obsDatetime"));
		cd.setMinTimeInPast(min);
		cd.setMinTimeInPastUnits(minUnits);
		cd.setMaxTimeInPast(max);
		cd.setMaxTimeInPastUnits(maxUnits);
		cd.setEffectiveDate(effectiveDate);

		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, new EvaluationContext());
		Assert.assertEquals("Expected " + ObjectUtil.toString(",", expectedPatients) + " but found " + c.getMemberIds(), expectedPatients.length, c.getSize());
		if (expectedPatients == null || expectedPatients.length == 0) {
			Assert.assertEquals("Did not expect any patients, but found " + c.getMemberIds(), 0, c.getSize());
		}
		for (Integer pId : expectedPatients) {
			Assert.assertTrue("Expected Cohort to contain " + pId, c.contains(pId));
		}
	}	
}
