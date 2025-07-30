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

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.api.EncounterService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.definition.ObsForVisitDataDefinition;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.data.visit.service.VisitDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.VisitEvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class ObsForVisitDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	@Autowired
	private EncounterService encounterService;
	
	@Autowired
	private VisitService visitService;
	
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
	}
	
	/**
	 * @see ObsForVisitDataEvaluator#evaluate(VisitDataDefinition,EvaluationContext)
	 * @verifies return the obs that match the passed definition configuration
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void evaluate_shouldReturnAllObssForAllVisits() throws Exception {
		
		VisitEvaluationContext context = new VisitEvaluationContext();
		context.setBaseCohort(new Cohort("7,21"));
		
		// Assign Visit 5 to Encounters 7 and 8
		Visit visit5 = visitService.getVisit(5);
		visit5.setPatient(data.getPatientService().getPatient(21));
		Encounter encounter8 = encounterService.getEncounter(8);
		encounter8.setVisit(visit5);
		Encounter encounter7 = encounterService.getEncounter(7);
		encounter7.setVisit(visit5);
	
		ObsForVisitDataDefinition d = new ObsForVisitDataDefinition();
		d.setQuestion(Context.getConceptService().getConcept(5089));
		
		EvaluatedVisitData vd = Context.getService(VisitDataService.class).evaluate(d, context);
		Assert.assertEquals(2, ((List) vd.getData().get(5)).size());
		
		d.setWhich(TimeQualifier.LAST);
		vd = Context.getService(VisitDataService.class).evaluate(d, context);
		Assert.assertEquals(150, ((Obs) vd.getData().get(5)).getValueNumeric().intValue());

		d.setWhich(TimeQualifier.FIRST);
		vd = Context.getService(VisitDataService.class).evaluate(d, context);
		Assert.assertEquals(80, ((Obs) vd.getData().get(5)).getValueNumeric().intValue());
		
	}
	
	/**
	 * @see ObsForVisitDataEvaluator#evaluate(VisitDataDefinition,EvaluationContext)
	 * @verifies return the obs that match the passed definition configuration, when the concept configured is a concept set 
	 */
	@Test
	public void evaluate_shouldSupportConceptSets() throws Exception {
		
		VisitEvaluationContext context = new VisitEvaluationContext();
		context.setBaseCohort(new Cohort("7,21"));
		
		// Assign Visit 5 to Encounter 4
		Visit visit5 = visitService.getVisit(5);
		visit5.setPatient(data.getPatientService().getPatient(21));
		Encounter encounter4 = encounterService.getEncounter(4);
		encounter4.setVisit(visit5);
		
		ObsForVisitDataDefinition def = new ObsForVisitDataDefinition();
		
		// Set the question as a concept set
		def.setQuestion(Context.getConceptService().getConcept(23));
		
		EvaluatedVisitData vd = Context.getService(VisitDataService.class).evaluate(def, context);
		Assert.assertEquals(3, ((List) vd.getData().get(5)).size());
	}
}