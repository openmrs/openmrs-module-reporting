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
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.PatientCharacteristicCohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Testing the cohort definition persister.  
 */
public class CohortDefinitionPersisterTest extends BaseModuleContextSensitiveTest {

	// Logger
	private Log log = LogFactory.getLog(this.getClass());
	
	//private SerializedObjectDAO dao = null;
	
	
	
	@Before
	public void runBeforeTest() throws Exception { 
		authenticate();		
		/*
		if (dao == null) {
			dao = (SerializedObjectDAO) applicationContext.getBean("serializedObjectDAO");
			dao.registerSupportedType(CohortDefinition.class);
		}*/	
	}
	
	
	
	@Test
	public void shouldSaveCohortDefinition() throws Exception { 		
		CohortDefinitionService service = Context.getService(CohortDefinitionService.class);
		PatientCharacteristicCohortDefinition cohortDefinition = new PatientCharacteristicCohortDefinition();		
		cohortDefinition.setName("Testing");
		//cohortDefinition.setCreator(Context.getAuthenticatedUser());
		//cohortDefinition.setChangedBy(Context.getAuthenticatedUser());
		
		
		CohortDefinition savedCohortDefinition = service.saveCohortDefinition(cohortDefinition);		
		//Context.getService(CohortDefinitionService.class).saveCohortDefinition(cohortDefinition);
		//dao.saveObject(cohortDefinition);
		Assert.assertTrue(savedCohortDefinition.getId()!=null);
	}
	
}