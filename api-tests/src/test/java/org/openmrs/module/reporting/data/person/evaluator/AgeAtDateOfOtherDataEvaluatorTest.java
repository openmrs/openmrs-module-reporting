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

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.AgeAtDateOfOtherDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test for the AgeAtDateOfOtherDataEvaluator
 */
public class AgeAtDateOfOtherDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	/**
	 * @see AgeAtDateOfOtherDataEvaluator#evaluate(PersonDataDefinition,EvaluationContext)
	 * @verifies return all ages on the date of the given definition
	 */
	@Test
	public void evaluate_shouldReturnAllAgesOnTheDateOfTheGivenDefinition() throws Exception {
		
		ObsForPersonDataDefinition lastWeight = new ObsForPersonDataDefinition();
		lastWeight.setWhich(TimeQualifier.LAST);
		lastWeight.setQuestion(Context.getConceptService().getConcept(5089));
		
		MappedData<ObsForPersonDataDefinition> mappedDef = new MappedData<ObsForPersonDataDefinition>();
		mappedDef.setParameterizable(lastWeight);
		mappedDef.addConverter(new PropertyConverter(Obs.class, "obsDatetime"));
		
		AgeAtDateOfOtherDataDefinition ageAtLastWeight = new AgeAtDateOfOtherDataDefinition();
		ageAtLastWeight.setEffectiveDateDefinition(mappedDef);
		
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("20,21,22"));
		EvaluatedPersonData data = Context.getService(PersonDataService.class).evaluate(ageAtLastWeight, context);
		
		Age pat20 = (Age)data.getData().get(20);
		Assert.assertEquals("1925-02-08", DateUtil.formatDate(pat20.getBirthDate(), "yyyy-MM-dd"));
		Assert.assertEquals("2009-08-19", DateUtil.formatDate(pat20.getCurrentDate(), "yyyy-MM-dd"));
		Assert.assertEquals(84, pat20.getFullYears().intValue());
		
		Age pat21 = (Age)data.getData().get(21);
		Assert.assertEquals("1959-06-08", DateUtil.formatDate(pat21.getBirthDate(), "yyyy-MM-dd"));
		Assert.assertEquals("2009-09-19", DateUtil.formatDate(pat21.getCurrentDate(), "yyyy-MM-dd"));
		Assert.assertEquals(50, pat21.getFullYears().intValue());
		
		Age pat22 = (Age)data.getData().get(22);
		Assert.assertEquals("1997-07-08", DateUtil.formatDate(pat22.getBirthDate(), "yyyy-MM-dd"));
		Assert.assertEquals("2009-09-19", DateUtil.formatDate(pat22.getCurrentDate(), "yyyy-MM-dd"));
		Assert.assertEquals(12, pat22.getFullYears().intValue());
	}
	
}