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