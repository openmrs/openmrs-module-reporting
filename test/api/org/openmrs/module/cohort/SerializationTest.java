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

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.serialization.xstream.XStreamSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Testing the cohort definition persister.  
 */
public class SerializationTest extends BaseModuleContextSensitiveTest {

	// Logger
	private Log log = LogFactory.getLog(this.getClass());
	
	@Before
	public void runBeforeTest() throws Exception { 
		authenticate();		
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
		Context.getSerializationService().serialize(pa, XStreamSerializer.class);
	}
	
	@Test
	public void shouldSaveAuthenticatedUser() throws Exception { 		
		User admin = Context.getAuthenticatedUser();
		Context.getSerializationService().serialize(admin, XStreamSerializer.class);		
	}

}