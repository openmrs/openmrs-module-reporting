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
package org.openmrs.module.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.report.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.serialization.xstream.XStreamShortSerializer;
import org.openmrs.reporting.AbstractReportObject;
import org.openmrs.reporting.ReportObjectService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Testing the cohort definition persister.  
 */
public class SerializationTest extends BaseModuleContextSensitiveTest {
	
	
	
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}


	// Logger
	private Log log = LogFactory.getLog(this.getClass());
		
	@Test
	public void shouldReturnDataExportsWithUuids() { 
		
		List<AbstractReportObject> reportObjects = Context.getService(ReportObjectService.class).getAllReportObjects();
		
		for (AbstractReportObject reportObject : reportObjects) { 
			log.info("object " + reportObject.getName() + " " + reportObject.getType() + " " + reportObject.getUuid());			
		}
		
	}
	
	
	@Test
	public void shouldReturnSameClassName(){
		User admin = Context.getAuthenticatedUser();
		Set<PersonName> set = admin.getNames();
		/*
		 * You can see the "standardTestDataSet.xml" file, admin's personName only have one record
		 * and that recode's person_name_id is "9348".
		 * 
		 * Besides, both "person_id" and "creator" of that record are equal.
		 */
		for(PersonName pn : set){
			String classNameOfCreator = pn.getCreator().getClass().getName();
			String classNameOfPerson = pn.getPerson().getClass().getName();
			System.out.println(classNameOfCreator);
			System.out.println(classNameOfPerson);
			assertEquals(classNameOfCreator, classNameOfPerson);
		}
	}
	
	@Test
	public void shouldSavePersonAddress() throws Exception{
		PersonAddress pa = Context.getPersonService().getPersonAddressByUuid("0908e42c-ff9b-4f0c-be5c-4a517e3feb34");
		assertTrue("pa's creator should be a cglib proxy, but it won't throw LazyInitializationException while it be serialized", pa.getCreator().getClass().getName().indexOf("EnhancerByCGLIB") != -1);
		//you can see the stack trace, LazyInitializationException is throwed while pa.creator.userProperties is serialized, not pa.creator
		Context.getSerializationService().serialize(pa, XStreamShortSerializer.class);
	}
	
	@Test
	public void shouldSaveAuthenticatedUser() throws Exception { 		
		User admin = Context.getAuthenticatedUser();
		Context.getSerializationService().serialize(admin, XStreamShortSerializer.class);		
	}

	
	@Test
	public void shouldSaveCohortDefinition() throws Exception { 
		AgeCohortDefinition cohortDefinition = new AgeCohortDefinition();
		cohortDefinition.setName("Test 1");
		CohortDefinition saved = Context.getService(CohortDefinitionService.class).saveCohortDefinition(cohortDefinition);
	}
	
	
	@Test
	public void shouldSaveCohortDefinitionInAnotherHibernateSession() throws Exception { 		

		UserContext userContext = null;
		
		// Authenticate in one request
		Context.openSession();
		Context.authenticate("admin", "test");
		userContext = Context.getUserContext();
		Context.closeSession();
				
		// Save a new cohort definition in another transaction using a cached user context
		Context.openSession();
		Context.setUserContext(userContext);		
		AgeCohortDefinition cohortDefinition = new AgeCohortDefinition();
		cohortDefinition.setName("Test 1");
		CohortDefinition saved = Context.getService(CohortDefinitionService.class).saveCohortDefinition(cohortDefinition);		
		log.info("Creator: " + saved.getCreator());
		Assert.assertEquals("Cohort definition creator should be the same as authenticated user", saved.getCreator(), Context.getAuthenticatedUser());
		Assert.assertNotNull("Cohort definition creator should not be null", saved.getCreator());
		Assert.assertEquals("Cohort definition creator should be the 'admin' user", Context.getAuthenticatedUser().getUsername(), "admin");
		Context.closeSession();
	}
	

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldSaveReportDefinition() throws Exception { 		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("Test 1");
		ReportDefinition saved = Context.getService(ReportService.class).saveReportDefinition(reportDefinition);
		Assert.assertNotNull(saved);
		
	}
	
	
	/*
	//@Before
	public void beforeTransaction() throws Exception { 
	
		authenticate();
	
		// if there isn't a userContext on the session yet, create one
		// and set it onto the session
		if (userContext == null) {
			userContext = new UserContext();
		}
		
		// Should only have to be called once
		if (!userContext.isAuthenticated()) {
			try { 
				authenticate();
			} catch (Exception e) { 
				log.error("Unable to authenticate user", e);
			}
		}
		
		// Add the user context to the current thread 
		Context.setUserContext(userContext);
		Thread.currentThread().setContextClassLoader(OpenmrsClassLoader.getInstance());
	}

	//@After 
	public void afterTransaction() { 		
		Context.clearUserContext();		
	}
	*/
	
	


	
	
}