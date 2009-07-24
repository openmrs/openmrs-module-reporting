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

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.cohort.definition.ProgramStateCohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.cohort.definition.util.CohortExpressionParser;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests methods in the {@link CohortUtil} class.
 */
public class CohortUtilTest extends BaseContextSensitiveTest {
	
	/**
	 * @see {@link CohortUtil#parse(String)}
	 */
	@Test
	/*
	 * TODO: This test currently breaks because the getCohortDefinitionByName method is not 
	 * implemented (and possibly due to other changes).  But the question is - do we want/need to 
	 * support this kind of expression parsing with cohort definitions?
	 * It's tricky to do, since many different providers can expose CohortDefinitions, 
	 * we need to think about whether the "name" of a CohortDefinition is going to be unique.
	 * It would be better to allow the Provider to expose a unique key & name for the cohort definitions
	 * that it can retrieve, and allow the user to pick from this list in the UI.  Then,
	 * the expression is built as a tree of Objects (BooleanOperators, CohortDefinitions, and possibly
	 * references to indexed CohortDefinitions in a CohortSearchHistory), and never from an string expression
	 */
	@Verifies(value = "should parse specification with and in it", method = "parse(String)")
	public void parse_shouldParseSpecificationWithAndInIt() throws Exception {
		// sets up the database

		// Create a search called "Male" 
		GenderCohortDefinition maleFilter = new GenderCohortDefinition();
		maleFilter.setName("Male");
		maleFilter.setGender("M");
		Context.getService(CohortDefinitionService.class).saveCohortDefinition(maleFilter);
		
		// Create a search called "EnrolledOnDate" with one parameter called untilDate
		ProgramStateCohortDefinition enrolledOnDateFilter = new ProgramStateCohortDefinition();
		enrolledOnDateFilter.setName("EnrolledOnDate");
		enrolledOnDateFilter.addParameter(new Parameter("untilDate", "Enrolled up until", Date.class, new Date(), false));
		Context.getService(CohortDefinitionService.class).saveCohortDefinition(enrolledOnDateFilter);
		
		List<Object> list = CohortExpressionParser.parseIntoTokens("[Male] and [EnrolledOnDate|untilDate=${report.startDate}]");

		{
			BaseCohortDefinition test = (BaseCohortDefinition) list.get(0);
			Assert.assertTrue(test instanceof GenderCohortDefinition);
			Assert.assertEquals(((GenderCohortDefinition) test).getGender(), "M");
		}
		assertEquals(list.get(1), PatientSetService.BooleanOperator.AND);
		{
			BaseCohortDefinition test = (BaseCohortDefinition) list.get(2);
			Assert.assertTrue(test instanceof ProgramStateCohortDefinition);
			Assert.assertEquals(1, test.getParameters().size());
		}
	}
	
}
