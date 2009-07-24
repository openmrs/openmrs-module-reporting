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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.cohort.definition.persister.CohortDefinitionPersister;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Testing the cohort definition persister.  
 */
public class CohortDefinitionServiceTest extends BaseModuleContextSensitiveTest {

	// Logger
	private Log log = LogFactory.getLog(this.getClass());	
	
	// Serialized Object DAO
	private SerializedObjectDAO dao = null;
	private CohortDefinitionPersister persister = null;
		
	@Before
	public void runBeforeTest() throws Exception { 
		authenticate();		
		if (dao == null) {
			dao = (SerializedObjectDAO) applicationContext.getBean("serializedObjectDAO");
			dao.registerSupportedType(CohortDefinition.class);
		}
		
		if (persister == null) { 
			persister = (CohortDefinitionPersister) applicationContext.getBean("serializedCohortDefinitionPersister");																							
		}
	}
	
	/**
	 * To run this method successfully, please firstly modify a place in "User.hbm.xml" as follow:
	 * 
	 * <map name="userProperties" table="user_property" lazy="false"
	 *		...
	 * </map>
	 * 
	 * We should let User#userProterties to be eager fetched.
	 * 
	 * Note: After we modified "User.hbm.xml", then should re-run "pacage-api" target in "build.xml" of trunk 1.5 and let reporting use the new openmrs-api-xxx.jar
	 * 
	 */
	@Test
	public void shouldSaveCohortDefinitionUsingServiceNoWithAuthenticatedUser(){
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		GenderCohortDefinition cohortDefinition = new GenderCohortDefinition();		
		cohortDefinition.setName("Testing");
		User admin = Context.getUserService().getUser(1);
		cohortDefinition.setCreator(admin);
		CohortDefinition savedCohortDefinition = service.saveCohortDefinition(cohortDefinition);		
		Assert.assertTrue(savedCohortDefinition.getId()!=null);
	}
	
	@Test
	public void shouldSaveCohortDefinitionUsingService() throws Exception { 		
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		GenderCohortDefinition cohortDefinition = new GenderCohortDefinition();		
		cohortDefinition.setName("Testing");
		CohortDefinition savedCohortDefinition = service.saveCohortDefinition(cohortDefinition);		
		Assert.assertTrue(savedCohortDefinition.getId()!=null);
	}

	@Test
	public void shouldSaveCohortDefinitionUsingPersister() throws Exception { 		
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		GenderCohortDefinition cohortDefinition = new GenderCohortDefinition();		
		cohortDefinition.setName("Testing");
		CohortDefinition savedCohortDefinition = persister.saveCohortDefinition(cohortDefinition);		
		Assert.assertTrue(savedCohortDefinition.getId()!=null);
	}
	
	@Test
	public void shouldSaveCohortDefinitionUsingDao() throws Exception { 		
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		GenderCohortDefinition cohortDefinition = new GenderCohortDefinition();		
		cohortDefinition.setName("Testing");
		CohortDefinition savedCohortDefinition = dao.saveObject(cohortDefinition);
		Assert.assertTrue(savedCohortDefinition.getId()!=null);
	}
	
	
}