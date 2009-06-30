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
package org.openmrs.module.cohort;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.ObsCohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.test.BaseContextSensitiveTest;

/**
 *
 */
public class PatientSearchTest extends BaseContextSensitiveTest {
	
	/**
	 * Set up the database with the initial dataset before every test method in this class.
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet("org/openmrs/report/include/PatientSearchTest.xml");
	}
	
	/**
	 * TODO: Make this use asserts instead of printing to stdout
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldParameters() throws Exception {
		ObsService obsService = Context.getObsService();
		
		// set the date of the first obs to be within the range of the param
		// (This date is outside of the "default" value, but inside the set value)
		Calendar inRange = new GregorianCalendar();
		inRange.add(Calendar.DATE, -45);
		Obs firstObs = obsService.getObs(1);
		firstObs.setObsDatetime(inRange.getTime());
		obsService.saveObs(firstObs, "Creating obs");
		
		// set the date of the second obs to be OUT of the range of the param
		Calendar outOfRange = new GregorianCalendar();
		outOfRange.add(Calendar.DATE, -95);
		Obs secondObs = obsService.getObs(2);
		secondObs.setObsDatetime(outOfRange.getTime());
		obsService.saveObs(secondObs, "Creating obs");
		
		Map<Parameter, Object> globalParamValues = new HashMap<Parameter, Object>();
		Parameter p = new Parameter("withinLastDays", "How many days?", Integer.class, 30, false, false);
		globalParamValues.put(p, 60);
		
		
		ObsCohortDefinition obsFilter = new ObsCohortDefinition();
		obsFilter.setTimeModifier(TimeModifier.ANY);
		obsFilter.setQuestion(Context.getConceptService().getConceptByName("CD4 COUNT"));
		obsFilter.addParameter(p);
		
		EvaluationContext ec = new EvaluationContext();
		for (Map.Entry<Parameter, Object> e : globalParamValues.entrySet()) {
			ec.addParameterValue(e.getKey().getName(), e.getValue());
		}
		
		Cohort result = Context.getService(CohortDefinitionService.class).evaluate(obsFilter, ec);
		
		//System.out.println("results is " + result.size());
		
		assertEquals(1, result.size());
		
		// make sure it was the patient#2 that was selected (the patient for the first obs)
		assertTrue(result.getMemberIds().contains(2));
		
	}
	
}
