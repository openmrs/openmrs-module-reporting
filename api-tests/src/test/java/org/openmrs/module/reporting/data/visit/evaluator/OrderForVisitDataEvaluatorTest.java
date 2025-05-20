/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
import org.openmrs.module.ModuleUtil;
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
import org.openmrs.util.OpenmrsConstants;
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
	
	private Integer expectedOrders;
	private Integer expectedOrdersWithType;

	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {

		if (ModuleUtil.compareVersion(OpenmrsConstants.OPENMRS_VERSION, "1.10") < 0) {
			setup1_9();
		} else {
			setup1_10();
		}
	}

	private void setup1_9() throws Exception {
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

		expectedOrders = 4;
		
		// Set Order Type 1 to the Order 3
		OrderType type1 = orderService.getOrderType(1);
		orderService.getOrder(3).setOrderType(type1);
		
		expectedOrdersWithType = 1;

	}


	private void setup1_10() throws Exception {
		// Assign Visit 1 to Encounter 6
		Visit visit1 = visitService.getVisit(1);
		Encounter encounter6 = encounterService.getEncounter(6);
		encounter6.setVisit(visit1);

		expectedOrders = 11;  
		
		expectedOrdersWithType = 8;
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
		Assert.assertEquals(expectedOrders.intValue(), ((List) vd.getData().get(1)).size());

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

		d.setTypes(Arrays.asList(orderService.getOrderType(1)));

		EvaluatedVisitData vd = Context.getService(VisitDataService.class).evaluate(d, context);
		Assert.assertEquals(expectedOrdersWithType.intValue(), ((List) vd.getData().get(1)).size());

	}

}