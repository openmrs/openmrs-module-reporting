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
package org.openmrs.module.reporting.data.patient.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DrugOrderSet;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.converter.ChainedConverter;
import org.openmrs.module.reporting.data.converter.CollectionConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.DrugOrdersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * DrugOrdersForPatientDataEvaluator tests
 */
@SkipBaseSetup
public class DrugOrdersForPatientDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
        initializeInMemoryDatabase();
	    authenticate();
	}
	
	/**
	 * @see DrugOrdersForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return drug orders restricted by drug
	 */
	@Test
	public void evaluate_shouldReturnDrugOrdersRestrictedByDrug() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));
		
		DrugOrdersForPatientDataDefinition def = new DrugOrdersForPatientDataDefinition();
		def.addDrugToInclude(Context.getConceptService().getDrug(2));
		DrugOrderSet history = (DrugOrderSet)Context.getService(PatientDataService.class).evaluate(def, context).getData().get(2);
		Assert.assertEquals(2, history.size());
		
		def.addDrugToInclude(Context.getConceptService().getDrug(3));
		history = (DrugOrderSet)Context.getService(PatientDataService.class).evaluate(def, context).getData().get(2);
		Assert.assertEquals(4, history.size());
		
		CollectionConverter drugOrderListConverter = new CollectionConverter(new ObjectFormatter("{drug}"), true, null);
		ObjectFormatter drugOrderFormatter = new ObjectFormatter(" + ");
		ChainedConverter c = new ChainedConverter(drugOrderListConverter, drugOrderFormatter);
	}

	/**
	 * @see DrugOrdersForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return drug orders restricted by drug concept
	 */
	@Test
	public void evaluate_shouldReturnDrugOrdersRestrictedByDrugConcept() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));
		
		DrugOrdersForPatientDataDefinition def = new DrugOrdersForPatientDataDefinition();
		def.addDrugConceptToInclude(Context.getConceptService().getConcept(792));
		EvaluatedPatientData evaluated = Context.getService(PatientDataService.class).evaluate(def, context);
		DrugOrderSet history = (DrugOrderSet)evaluated.getData().get(2);
		Assert.assertEquals(2, history.size());
		
		def.addDrugConceptToInclude(Context.getConceptService().getConcept(88));
		evaluated = Context.getService(PatientDataService.class).evaluate(def, context);
		history = (DrugOrderSet)evaluated.getData().get(2);
		Assert.assertEquals(4, history.size());
	}

	/**
	 * @see DrugOrdersForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return drug orders restricted by drug concept set
	 */
	@Test
	public void evaluate_shouldReturnDrugOrdersRestrictedByDrugConceptSet() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));
		
		DrugOrdersForPatientDataDefinition def = new DrugOrdersForPatientDataDefinition();
		def.addDrugConceptSetToInclude(Context.getConceptService().getConcept(24));
		DrugOrderSet history = (DrugOrderSet)Context.getService(PatientDataService.class).evaluate(def, context).getData().get(2);
		Assert.assertEquals(4, history.size());
	}

	/**
	 * @see DrugOrdersForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return drug orders active on a particular date
	 */
	@Test
	public void evaluate_shouldReturnDrugOrdersActiveOnAParticularDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));
		
		DrugOrdersForPatientDataDefinition def = new DrugOrdersForPatientDataDefinition();
		
		def.setActiveOnDate(DateUtil.getDateTime(2008, 8, 5));
		DrugOrderSet history = (DrugOrderSet)Context.getService(PatientDataService.class).evaluate(def, context).getData().get(2);
		Assert.assertEquals(1, history.size());
		Assert.assertEquals(2, history.iterator().next().getOrderId().intValue());

		// Edge case where a drug is changed on this date
		def.setActiveOnDate(DateUtil.getDateTime(2008, 8, 8));
		history = (DrugOrderSet)Context.getService(PatientDataService.class).evaluate(def, context).getData().get(2);
		Assert.assertEquals(1, history.size());
		Assert.assertEquals(3, history.iterator().next().getOrderId().intValue());
		
		def.setActiveOnDate(DateUtil.getDateTime(2008, 8, 19));
		history = (DrugOrderSet)Context.getService(PatientDataService.class).evaluate(def, context).getData().get(2);
		Assert.assertEquals(2, history.size());
	}

	/**
	 * @see DrugOrdersForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return drug orders started on or before a given date
	 */
	@Test
	public void evaluate_shouldReturnDrugOrdersStartedOnOrBeforeAGivenDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));
		
		DrugOrdersForPatientDataDefinition def = new DrugOrdersForPatientDataDefinition();
		
		def.setStartedOnOrBefore(DateUtil.getDateTime(2008, 7, 1));
		DrugOrderSet history = (DrugOrderSet)Context.getService(PatientDataService.class).evaluate(def, context).getData().get(2);
		Assert.assertEquals(1, history.size());

		def.setStartedOnOrBefore(DateUtil.getDateTime(2008, 8, 1));
		history = (DrugOrderSet)Context.getService(PatientDataService.class).evaluate(def, context).getData().get(2);
		Assert.assertEquals(2, history.size());
		
		def.setStartedOnOrBefore(DateUtil.getDateTime(2008, 9, 1));
		history = (DrugOrderSet)Context.getService(PatientDataService.class).evaluate(def, context).getData().get(2);
		Assert.assertEquals(4, history.size());
	}

	/**
	 * @see DrugOrdersForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return drug orders started on or after a given date
	 */
	@Test
	public void evaluate_shouldReturnDrugOrdersStartedOnOrAfterAGivenDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));
		
		DrugOrdersForPatientDataDefinition def = new DrugOrdersForPatientDataDefinition();
		
		def.setStartedOnOrAfter(DateUtil.getDateTime(2007, 8, 1));
		DrugOrderSet history = (DrugOrderSet)Context.getService(PatientDataService.class).evaluate(def, context).getData().get(2);
		Assert.assertEquals(4, history.size());

		def.setStartedOnOrAfter(DateUtil.getDateTime(2008, 8, 1));
		history = (DrugOrderSet)Context.getService(PatientDataService.class).evaluate(def, context).getData().get(2);
		Assert.assertEquals(3, history.size());
		
		def.setStartedOnOrAfter(DateUtil.getDateTime(2008, 8, 19));
		history = (DrugOrderSet)Context.getService(PatientDataService.class).evaluate(def, context).getData().get(2);
		Assert.assertEquals(1, history.size());	
	}

	/**
	 * @see DrugOrdersForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return drug orders completed on or before a given date
	 */
	@Test
	public void evaluate_shouldReturnDrugOrdersCompletedOnOrBeforeAGivenDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));
		
		DrugOrdersForPatientDataDefinition def = new DrugOrdersForPatientDataDefinition();
		
		def.setCompletedOnOrBefore(DateUtil.getDateTime(2007, 8, 7));
		DrugOrderSet history = (DrugOrderSet)Context.getService(PatientDataService.class).evaluate(def, context).getData().get(2);
		Assert.assertNull(history);

		def.setCompletedOnOrBefore(DateUtil.getDateTime(2008, 8, 7));
		history = (DrugOrderSet)Context.getService(PatientDataService.class).evaluate(def, context).getData().get(2);
		Assert.assertEquals(1, history.size());
		
		def.setCompletedOnOrBefore(DateUtil.getDateTime(2008, 8, 8));
		history = (DrugOrderSet)Context.getService(PatientDataService.class).evaluate(def, context).getData().get(2);
		Assert.assertEquals(2, history.size());	
	}

	/**
	 * @see DrugOrdersForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return drug orders completed on or after a given date
	 */
	@Test
	public void evaluate_shouldReturnDrugOrdersCompletedOnOrAfterAGivenDate() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2"));
		
		DrugOrdersForPatientDataDefinition def = new DrugOrdersForPatientDataDefinition();
		
		def.setCompletedOnOrAfter(DateUtil.getDateTime(2009, 8, 7));
		DrugOrderSet history = (DrugOrderSet)Context.getService(PatientDataService.class).evaluate(def, context).getData().get(2);
		Assert.assertNull(history);

		def.setCompletedOnOrAfter(DateUtil.getDateTime(2008, 8, 7));
		history = (DrugOrderSet)Context.getService(PatientDataService.class).evaluate(def, context).getData().get(2);
		Assert.assertEquals(1, history.size());
		
		def.setCompletedOnOrAfter(DateUtil.getDateTime(2007, 8, 7));
		history = (DrugOrderSet)Context.getService(PatientDataService.class).evaluate(def, context).getData().get(2);
		Assert.assertEquals(2, history.size());	
	}
}