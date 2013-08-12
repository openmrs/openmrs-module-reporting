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

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class ObsForPersonDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see ObsForPersonDataEvaluator#evaluate(PersonDataDefinition,EvaluationContext)
	 * @verifies return the obs that match the passed definition configuration
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void evaluate_shouldReturnAllObssForAllPersons() throws Exception {
		
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("7,20"));
		
		ObsForPersonDataDefinition d = new ObsForPersonDataDefinition();
		d.setQuestion(Context.getConceptService().getConcept(5089));
		
		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals(3, ((List)pd.getData().get(7)).size());
		Assert.assertEquals(1, ((List)pd.getData().get(20)).size());
		
		d.setOnOrAfter(DateUtil.getDateTime(2008, 8, 1));
		d.setOnOrBefore(DateUtil.getDateTime(2008, 8, 15));
		pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals(2, ((List)pd.getData().get(7)).size());
		Assert.assertNull(pd.getData().get(20));
		
		d.setWhich(TimeQualifier.LAST);
		d.setOnOrAfter(null);
		d.setOnOrBefore(null);
		pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals(61, ((Obs)pd.getData().get(7)).getValueNumeric().intValue());
		Assert.assertEquals(180, ((Obs)pd.getData().get(20)).getValueNumeric().intValue());
		
		d.setWhich(TimeQualifier.FIRST);
		d.setOnOrAfter(null);
		d.setOnOrBefore(null);
		pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals(50, ((Obs)pd.getData().get(7)).getValueNumeric().intValue());
		Assert.assertEquals(180, ((Obs)pd.getData().get(20)).getValueNumeric().intValue());
		
		d.setWhich(TimeQualifier.ANY);
		d.setOnOrAfter(null);
		d.setOnOrBefore(null);
		d.addEncounterType(Context.getEncounterService().getEncounterType(1));
		pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals(2, ((List)pd.getData().get(7)).size());
			
		d.addEncounterType(Context.getEncounterService().getEncounterType(1));
		d.addEncounterType(Context.getEncounterService().getEncounterType(2));
		pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals(3, ((List)pd.getData().get(7)).size());
	
		d.addEncounterType(Context.getEncounterService().getEncounterType(1));
		d.addEncounterType(Context.getEncounterService().getEncounterType(2));
		d.setFormList(Collections.singletonList(new Form(1)));
		pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertEquals(3, ((List)pd.getData().get(7)).size());
		
		d.addEncounterType(Context.getEncounterService().getEncounterType(1));
		d.addEncounterType(Context.getEncounterService().getEncounterType(2));
		d.setFormList(Collections.singletonList(new Form(2)));
		pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assert.assertNull(pd.getData().get(7));
		
	}
}