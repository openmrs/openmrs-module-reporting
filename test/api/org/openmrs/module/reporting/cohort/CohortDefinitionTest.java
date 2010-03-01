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
package org.openmrs.module.reporting.cohort;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Drug;
import org.openmrs.User;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DrugOrderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 *
 */
public class CohortDefinitionTest extends BaseModuleContextSensitiveTest {
	
	private Log log = LogFactory.getLog(this.getClass());
	DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
    public Boolean useInMemoryDatabase() {
	    return true;
	}	
	
	@Test
	public void shouldSaveCohortDefinition() throws Exception { 
		authenticate();
		
		CohortDefinitionService service = 
			Context.getService(CohortDefinitionService.class);
		
		List<CohortDefinition> cohortDefinitions = service.getAllCohortDefinitions(false);
		log.info("cohort definitions: " + cohortDefinitions.size() );
		
		GenderCohortDefinition cohortDefinition = 
			GenderCohortDefinition.class.newInstance();

		
		cohortDefinition.setName("Testing");

		User user = Context.getUserService().getUserByUsername("admin");
		log.info("User properties: " + user.getUserProperties());
		cohortDefinition.setCreator(null);
		cohortDefinition.setChangedBy(null);
		service.saveCohortDefinition(cohortDefinition);		
		cohortDefinitions = service.getAllCohortDefinitions(false);
		
		log.info("cohort definitions: " + cohortDefinitions.size() );
	}
	
	
	@Test
	public void should_acceptAnnotatedParameters() throws Exception {
		GenderCohortDefinition def = new GenderCohortDefinition();
		System.out.println(def.getParameters());
	}
	
	@Test
	public void shouldDrugOrderCohortDefinition() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/cohort/include/CohortDefinitionTest.xml");
		authenticate();
		
		EvaluationContext ec = null;
		Drug inh = Context.getConceptService().getDrug("INH 300mg");
		DrugOrderCohortDefinition filter = new DrugOrderCohortDefinition();
		filter.setAnyOrAll(PatientSetService.GroupMethod.ANY);
		Parameter sinceParam = new Parameter("sinceDate", "Since", Date.class);
		Parameter untilParam = new Parameter("untilDate", "Until", Date.class);
		filter.addParameter(sinceParam);
		filter.addParameter(untilParam);
		List<Drug> drugList = new ArrayList<Drug>();
		drugList.addAll(Collections.singletonList(inh));
		filter.setDrugList(drugList);
		
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(filter, ec);
		Assert.assertEquals("No dates should get 1", 1, cohort.size());				

		ec = createContext(untilParam, "2004-06-01", null);
		cohort = Context.getService(CohortDefinitionService.class).evaluate(filter, ec);
		Assert.assertEquals("Until before should get 0", 0, cohort.size());
		
		ec = createContext(untilParam, "2005-06-01", null);
		cohort = Context.getService(CohortDefinitionService.class).evaluate(filter, ec);
		Assert.assertEquals("Until during should get 1", 1, cohort.size());

		ec = createContext(untilParam, "2006-06-01", null);
		cohort = Context.getService(CohortDefinitionService.class).evaluate(filter, ec);
		Assert.assertEquals("Until after should get 1", 1, cohort.size());

		ec = createContext(sinceParam, null, "2004-06-01");
		cohort = Context.getService(CohortDefinitionService.class).evaluate(filter, ec);
		Assert.assertEquals("since before should get 1", 1, cohort.size());

		ec = createContext(sinceParam, null, "2005-06-01");
		cohort = Context.getService(CohortDefinitionService.class).evaluate(filter, ec);
		Assert.assertEquals("since during should get 1", 1, cohort.size());
		
		ec = createContext(sinceParam, null, "2006-06-01");
		cohort = Context.getService(CohortDefinitionService.class).evaluate(filter, ec);
		Assert.assertEquals("since after should get 0", 0, cohort.size());
	}

	public EvaluationContext createContext(Parameter p, String untilDate, String sinceDate) throws Exception {
		EvaluationContext ec = new EvaluationContext();
		if (untilDate != null) {
			ec.addParameterValue(p.getName(), ymd.parse(untilDate));
		}
		if (sinceDate != null) {
			ec.addParameterValue(p.getName(), ymd.parse(sinceDate));
		}
		return ec;
	}
}
