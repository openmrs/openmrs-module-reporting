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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObsActiveList;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.ObsActiveListPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests ObsActiveListPersonDataEvaluator
 */
public class ObsActiveListPersonDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
		
		// 2 added, none removed
		saveObs(7, "2012-01-01", 10001, 792);
		saveObs(7, "2012-02-01", 10001, 88);
		
		// 2 added, both removed
		saveObs(20, "2012-01-01", 10001, 792);
		saveObs(20, "2012-02-01", 10001, 88);
		saveObs(20, "2012-03-01", 10002, 792);
		saveObs(20, "2012-04-01", 10002, 88);
		
		// 2 added, both removed, one re-added
		saveObs(21, "2012-01-01", 10001, 792);
		saveObs(21, "2012-02-01", 10001, 88);
		saveObs(21, "2012-03-01", 10002, 792);
		saveObs(21, "2012-04-01", 10002, 88);
		saveObs(21, "2012-04-01", 10001, 792);	
	}
	
	/**
	 * @see ObsActiveListPersonDataEvaluator#evaluate(PersonDataDefinition,EvaluationContext)
	 * @verifies return the obs that match the passed definition configuration
	 */
	@Test
	public void evaluate_shouldReturnAllProblemLists() throws Exception {
		
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("7,20,21,22"));
		
		ObsActiveListPersonDataDefinition d = new ObsActiveListPersonDataDefinition();
		
		List<Concept> problemsAdded = new ArrayList<Concept>();
		problemsAdded.add(Context.getConceptService().getConcept(10001));
		d.setStartingConcepts(problemsAdded);
		
		List<Concept> problemsResolved = new ArrayList<Concept>();
		problemsResolved.add(Context.getConceptService().getConcept(10002));
		d.setEndingConcepts(problemsResolved);
		
		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(d, context);
		checkList(pd, 7, 792, 88);
		checkList(pd, 20);
		checkList(pd, 21, 792);
		Assert.assertNull(pd.getData().get(22));
	}
	
	private void saveObs(Integer personId, String dateStr, Integer question, Integer answer) {
		Person p = Context.getPersonService().getPerson(personId);
		Date d = DateUtil.parseDate(dateStr, "yyyy-MM-dd");
		Concept q = Context.getConceptService().getConcept(question);
		Concept a = Context.getConceptService().getConcept(answer);
		Location l = Context.getLocationService().getLocation(1);
		Obs o = new Obs(p, q, d, l);
		o.setValueCoded(a);
		Context.getObsService().saveObs(o, "Test");
	}
	
	private void checkList(EvaluatedPersonData pd, Integer patientId, Integer...problemIds) {
		Object o = pd.getData().get(patientId);
		ObsActiveList l = (ObsActiveList)o;
		Assert.assertEquals(l.getActiveItems().size(), problemIds.length);
		List<Integer> problemIdList = Arrays.asList(problemIds);
		for (Obs obs : l.getActiveItems()) {
			Assert.assertTrue(problemIdList.contains(obs.getValueCoded().getConceptId()));
		}
	}
}
