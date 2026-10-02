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

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.openmrs.test.jupiter.BaseContextSensitiveTest;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

public class ObsForPersonDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
		Assertions.assertEquals(3, ((List) pd.getData().get(7)).size());
		Assertions.assertEquals(1, ((List) pd.getData().get(20)).size());
		
		d.setOnOrAfter(DateUtil.getDateTime(2008, 8, 1));
		d.setOnOrBefore(DateUtil.getDateTime(2008, 8, 15));
		pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assertions.assertEquals(2, ((List) pd.getData().get(7)).size());
		Assertions.assertNull(pd.getData().get(20));
		
		d.setWhich(TimeQualifier.LAST);
		d.setOnOrAfter(null);
		d.setOnOrBefore(null);
		pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assertions.assertEquals(61, ((Obs) pd.getData().get(7)).getValueNumeric().intValue());
		Assertions.assertEquals(180, ((Obs) pd.getData().get(20)).getValueNumeric().intValue());
		
		d.setWhich(TimeQualifier.FIRST);
		d.setOnOrAfter(null);
		d.setOnOrBefore(null);
		pd = Context.getService(PersonDataService.class).evaluate(d, context);
		Assertions.assertEquals(50, ((Obs) pd.getData().get(7)).getValueNumeric().intValue());
		Assertions.assertEquals(180, ((Obs) pd.getData().get(20)).getValueNumeric().intValue());
		
	}
	
	/**
	 * @see ObsForPersonDataEvaluator#evaluate(PersonDataDefinition,EvaluationContext)
	 * @verifies return the obs that match the passed definition configuration
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void evaluate_shouldLimitObsByEncounterType() throws Exception {
		
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("7,20"));
		
		//By not limiting by encounter type we get back all Obs (3 Obs) for the specified question
		{
			ObsForPersonDataDefinition d = new ObsForPersonDataDefinition();
			d.setQuestion(Context.getConceptService().getConcept(5089));
			EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
			Assertions.assertEquals(3, ((List) pd.getData().get(7)).size());
		}
		
		//By limiting by a first encounter type (with encounter_type="2") we get back 1 Obs
		{
			ObsForPersonDataDefinition d = new ObsForPersonDataDefinition();
			d.setQuestion(Context.getConceptService().getConcept(5089));
			d.addEncounterType(Context.getEncounterService().getEncounterType(2));
			EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
			Assertions.assertEquals(1, ((List) pd.getData().get(7)).size());
		}
		
		//By limiting by a second encounter type (with encounter_type="1") we get back 2 Obs
		{
			ObsForPersonDataDefinition d = new ObsForPersonDataDefinition();
			d.setQuestion(Context.getConceptService().getConcept(5089));
			d.addEncounterType(Context.getEncounterService().getEncounterType(1));
			EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
			Assertions.assertEquals(2, ((List) pd.getData().get(7)).size());
		}
		
		//By adding both encounter types we get back 3 Obs
		{
			ObsForPersonDataDefinition d = new ObsForPersonDataDefinition();
			d.setQuestion(Context.getConceptService().getConcept(5089));
			d.addEncounterType(Context.getEncounterService().getEncounterType(1));
			d.addEncounterType(Context.getEncounterService().getEncounterType(2));
			EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
			Assertions.assertEquals(3, ((List) pd.getData().get(7)).size());
		}
	}
	
	/**
	 * @see ObsForPersonDataEvaluator#evaluate(PersonDataDefinition,EvaluationContext)
	 * @verifies return the obs that match the passed definition configuration
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void evaluate_shouldLimitObsByForm() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("7,20"));
		
		//By not limiting by form we get back all Obs (3 Obs) for the specified question
		{
			ObsForPersonDataDefinition d = new ObsForPersonDataDefinition();
			d.setQuestion(Context.getConceptService().getConcept(5089));
			EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
			Assertions.assertEquals(3, ((List) pd.getData().get(7)).size());
		}
		
		//By limiting by a first form (with form_id="3") we shouldn't get any Obs because there is no encounter with form_id="3" in our test dataset
		{
			ObsForPersonDataDefinition d = new ObsForPersonDataDefinition();
			d.setQuestion(Context.getConceptService().getConcept(5089));
			d.addForm(new Form(3));
			EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
			Assertions.assertNull(pd.getData().get(7));
		}
		
		//By limiting by a second form (with form_id="2") we get back 3 Obs because all encounters in our test dataset for the specified Obs question have been entered through this form
		{
			ObsForPersonDataDefinition d = new ObsForPersonDataDefinition();
			d.setQuestion(Context.getConceptService().getConcept(5089));
			d.addForm(new Form(2));
			EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
			Assertions.assertEquals(3, ((List) pd.getData().get(7)).size());
		}
	}
}
