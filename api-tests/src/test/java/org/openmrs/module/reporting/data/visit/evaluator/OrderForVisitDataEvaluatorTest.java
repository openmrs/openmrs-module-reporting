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
package org.openmrs.module.reporting.data.visit.evaluator;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.OrderType;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.definition.OrderForVisitDataDefinition;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.data.visit.service.VisitDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.VisitEvaluationContext;
import org.openmrs.module.reporting.query.visit.VisitIdSet;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderForVisitDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	@Autowired
	private EncounterService encounterService;

	@Autowired
	private VisitService visitService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	TestDataManager data;
 
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
		
		Visit visit1 = visitService.getVisit(1);
		Encounter encounter6 = encounterService.getEncounter(6);

		// Assign Encounters 6 to Orders 2, 3, 4 & 5
		orderService.getOrder(2).setEncounter(encounter6);
		orderService.getOrder(3).setEncounter(encounter6);
		orderService.getOrder(4).setEncounter(encounter6);
		orderService.getOrder(5).setEncounter(encounter6);
		
		// Assign Visit 1 to Encounter 6
		encounter6.setVisit(visit1);
		
	}
	
	/**
	 * @see OrderForVisitDataEvaluator#evaluate(VisitDataDefinition,EvaluationContext)
	 * @verifies return the orders that match the passed definition configuration
	 */
	@Test
	public void evaluate_shouldReturnAllOrdersForAVisit() throws Exception {
		
		VisitEvaluationContext context = new VisitEvaluationContext();
		context.setBaseVisits(new VisitIdSet(1));

		OrderForVisitDataDefinition d = new OrderForVisitDataDefinition();
		
		EvaluatedVisitData vd = Context.getService(VisitDataService.class).evaluate(d, context);
		Assert.assertEquals(4, ((List) vd.getData().get(1)).size());
		
	}

	/**
	 * @see OrderForVisitDataEvaluator#evaluate(VisitDataDefinition,EvaluationContext)
	 * @verifies return the orders that match the passed definition configuration, filtered by OrderType
	 */
	@Test
	public void evaluate_shouldFilterByType() throws Exception {
		
		VisitEvaluationContext context = new VisitEvaluationContext();
		context.setBaseVisits(new VisitIdSet(1));

		OrderForVisitDataDefinition d = new OrderForVisitDataDefinition();
		
		// Set Order Type 1 to the Order 3
		OrderType type1 = orderService.getOrderType(1);
		orderService.getOrder(3).setOrderType(type1);
		d.setTypes(Arrays.asList(type1));
		
		EvaluatedVisitData vd = Context.getService(VisitDataService.class).evaluate(d, context);
		Assert.assertEquals(1, ((List) vd.getData().get(1)).size());
		
	}
	
	
}