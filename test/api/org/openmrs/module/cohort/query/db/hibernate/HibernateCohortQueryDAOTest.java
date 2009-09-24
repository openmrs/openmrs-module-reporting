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
package org.openmrs.module.cohort.query.db.hibernate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.EncounterDAO;
import org.openmrs.api.db.hibernate.HibernateEncounterDAO;
import org.openmrs.module.cohort.query.db.CohortQueryDAO;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * This class tests the {@link EncounterDAO} linked to from the Context. Currently that file is the
 * {@link HibernateEncounterDAO} This should only have to test methods that don't really have
 * equivalents at the {@link EncounterService} layer.
 */
public class HibernateCohortQueryDAOTest extends BaseModuleContextSensitiveTest {
	
	protected static final Log log = LogFactory.getLog(HibernateCohortQueryDAOTest.class);
	
	private CohortQueryDAO dao = null;
	
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		//initializeInMemoryDatabase();
		//executeDataSet("org/openmrs/module/cohort/include/CohortQueryTest.xml");
		authenticate();
		if (dao == null)
			// fetch the dao from the spring application context
			// this bean name matches the name in /metadata/spring/applicationContext-service.xml
			dao = (CohortQueryDAO) applicationContext.getBean("cohortQueryDAO");
	}
	
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldGetAllPatientsEnrolledInAnyProgramEver() throws Exception { 		
		Cohort cohort = dao.getPatientsHavingStartedPrograms(new ArrayList<Program>(), null, null);
		log.warn("cohort: " + cohort.size());
	}	
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldGetAllPatientsEverEnrolledInHivProgram() throws Exception {		
		Program program = Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM");
		List<Program> programs = new ArrayList<Program>();
		programs.add(program);
		Cohort cohort = dao.getPatientsHavingStartedPrograms(programs, null, null);
		log.warn("cohort: " + cohort.size());
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldGetAllPatientsEnrolledInHivProgramAfterGivenDate() throws Exception {		
		Program program = Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM");
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2008, 0, 1); 
		List<Program> programs = new ArrayList<Program>();
		programs.add(program);
		Cohort cohort = dao.getPatientsHavingStartedPrograms(programs, calendar.getTime(), null);
		log.warn("cohort: " + cohort.size());
	}	
	
	/**
	 * TODO Why does the following query work, but this test fails?
	 * 
	 * select * from patient_program pp 
	 * where pp.voided = false  
	 * and pp.program_id in (3) 
	 * and pp.date_enrolled >= '2008-01-01' 
	 * and pp.date_enrolled <= '2008-12-31'
	 * group by pp.patient_id
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldGetAllPatientsEnrolledHivProgramBetweenDates() throws Exception {		
		Program program = Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM");
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2008, 0, 1); 
		Date enrolledOnOrAfter = calendar.getTime();
		calendar.set(2008, 0, 31);
		Date enrolledOnOrBefore = calendar.getTime();
		
		List<Program> programs = new ArrayList<Program>();
		programs.add(program);
		Cohort cohort = dao.getPatientsHavingStartedPrograms(programs, enrolledOnOrAfter, enrolledOnOrBefore);
		log.warn("cohort: " + cohort.size());
	}		
	

	
}
