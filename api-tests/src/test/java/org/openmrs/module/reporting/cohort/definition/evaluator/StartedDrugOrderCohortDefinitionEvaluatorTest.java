/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.reporting.cohort.definition.evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Drug;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.StartedDrugOrderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class StartedDrugOrderCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	@Test
	@Verifies(value = "should return all patients drugs active on or before a specific time", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsTakingDrugsActiveOnOrBeforeDate() throws Exception {
		StartedDrugOrderCohortDefinition cd = new StartedDrugOrderCohortDefinition();
		cd.setActiveOnOrBefore(DateUtil.getDateTime(2008, 8, 8));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertTrue(c.contains(2));
		Assert.assertTrue(c.contains(7));
		Assert.assertEquals(2, c.size());
	}
	
	@Test
	@Verifies(value = "should return all patients drugs active on or after a specific time", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsTakingDrugsActiveOnOrAfterDate() throws Exception {
		StartedDrugOrderCohortDefinition cd = new StartedDrugOrderCohortDefinition();
		cd.setActiveOnOrAfter(DateUtil.getDateTime(2010, 10, 1));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(3, c.size());
		Assert.assertTrue(c.contains(2));
		Assert.assertTrue(c.contains(21));
		Assert.assertTrue(c.contains(999));
	}
	
	@Test
	@Verifies(value = "should return all patients with a started drug order ", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsWithStartedDrugOrder() throws Exception {
		StartedDrugOrderCohortDefinition cd = new StartedDrugOrderCohortDefinition();
		cd.setDrugSet(new ArrayList<Drug>());
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(4, c.size());
		Assert.assertTrue(c.contains(2));
		Assert.assertTrue(c.contains(21));
		Assert.assertTrue(c.contains(999));
		Assert.assertTrue(c.contains(7));
	}
	
	@Test
	@Verifies(value = "should return all patients with a started drug order that mactches the drugSet ", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnAllPatientsWithMatchingDrugSet() throws Exception {
		StartedDrugOrderCohortDefinition cd = new StartedDrugOrderCohortDefinition();
		cd.addDrugSet(new Drug(3));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(2, c.size());
		Assert.assertTrue(c.contains(2));
		Assert.assertTrue(c.contains(7));
	}
}
