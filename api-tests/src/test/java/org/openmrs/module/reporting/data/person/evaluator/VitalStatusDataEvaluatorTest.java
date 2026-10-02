/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.person.evaluator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.common.VitalStatus;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.VitalStatusDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.jupiter.BaseContextSensitiveTest;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

public class VitalStatusDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	/**
	 * Run this before each unit test in this class. The "@BeforeEach" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@BeforeEach
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	/**
	 * @see BirthdateDataEvaluator#evaluate(PersonDataDefinition,EvaluationContext)
	 * @verifies return vital status for all persons
	 */
	@Test
	public void evaluate_shouldReturnVitalStatusForAllPersons() throws Exception {
		VitalStatusDataDefinition d = new VitalStatusDataDefinition();
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("20,21"));
		Concept unknown = Context.getConceptService().getConcept(22);

		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assertions.assertEquals(2, pd.getData().size());

		VitalStatus deadStatus = (VitalStatus)pd.getData().get(20);
		Assertions.assertEquals(true, deadStatus.getDead());
		Assertions.assertEquals("2005-02-08", DateUtil.formatDate(deadStatus.getDeathDate(), "yyyy-MM-dd"));
		Assertions.assertEquals(unknown, deadStatus.getCauseOfDeath());

		VitalStatus alive = (VitalStatus)pd.getData().get(21);
		Assertions.assertEquals(false, alive.getDead());
		Assertions.assertNull(alive.getDeathDate());
		Assertions.assertNull(alive.getCauseOfDeath());
	}
}