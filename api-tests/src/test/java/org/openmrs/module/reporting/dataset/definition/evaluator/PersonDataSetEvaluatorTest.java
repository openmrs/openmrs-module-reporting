/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.test.jupiter.BaseContextSensitiveTest;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

public class PersonDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	
	@Test
	public void evaluate_shouldEvaluateDataSetDefinition() throws Exception {
		
		/*
		RowPerPersonDataSetDefinition d = new RowPerPersonDataSetDefinition();
		d.addColumnDefinition(new PersonIdColumnDefinition("Person ID"));
		d.addColumnDefinition(new GenderColumnDefinition("Sexe"));
		d.addColumnDefinition(new AgeColumnDefinition("Age"));
		
		EvaluationContext context = new EvaluationContext();
		RowPerObjectDataSet dataset = (RowPerObjectDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, context);
		Assertions.assertEquals("M", dataset.getColumnValue(2, "Sexe"));
		Assertions.assertEquals("F", dataset.getColumnValue(7, "Sexe"));
		Assertions.assertEquals(13, dataset.getRows().size());
		
		PersonEvaluationContext pec = new PersonEvaluationContext();
		PersonQueryResult personQuery = new PersonQueryResult();
		personQuery.add(2,6,8,501);
		pec.setBasePersons(personQuery);
		
		dataset = (RowPerObjectDataSet)Context.getService(DataSetDefinitionService.class).evaluate(d, pec);
		Assertions.assertEquals("M", dataset.getColumnValue(2, "Sexe"));
		Assertions.assertNull(dataset.getColumnValue(7, "Sexe"));
		Assertions.assertEquals(4, dataset.getRows().size());
		*/
	}
}