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
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.Drug;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
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
		
	@Test
	public void shouldGetDrugs() { 
		Concept arvDrugs = Context.getConceptService().getConceptByName("ANTIRETROVIRAL DRUGS");
		List<ConceptSet> drugSets = Context.getConceptService().getConceptSetsByConcept(arvDrugs);				
	    if (drugSets != null) {
	    	for (ConceptSet drugSet : drugSets) {

	    		log.info("Drug concept " + drugSet.getConcept().getName().getName());

	    		List<Drug> drugs = Context.getConceptService().getDrugsByConcept(drugSet.getConcept());
	    		
	    		for(Drug drug : drugs) { 
	    			
	    			log.info("Drug: " + drug.getDrugId());
	    		}
			}
    	}		
	}
	
	
	/**
	 * @throws Exception
	 */
	@Test
	public void shouldGetPatientsHavingStartedHivProgram() throws Exception {		
		Program program = Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM");
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2007, 0, 1); 
		Date enrolledOnOrAfter = calendar.getTime();
		calendar.set(2007, 0, 31);
		Date enrolledOnOrBefore = calendar.getTime();
		
		List<Program> programs = new ArrayList<Program>();
		programs.add(program);
		
		Cohort cohort = dao.getPatientsHavingProgramEnrollment(programs, null, null, null, null);
		log.warn("# Patients ever enrolled in HIV PROGRAM: " + cohort.size());

		cohort = dao.getPatientsHavingProgramEnrollment(programs, calendar.getTime(), null, null, null);
		log.warn("# Patients ever enrolled in HIV PROGRAM on or after date: " + cohort.size());

		cohort = dao.getPatientsHavingProgramEnrollment(programs, enrolledOnOrAfter, enrolledOnOrBefore, null, null);
		log.warn("# Patients ever enrolled in HIV PROGRAM during period: " + cohort.size());
	}		
	

	/**
	 * @throws Exception
	 */
	@Test
	public void shouldGetPatientsHavingCompletedHivProgram() throws Exception {		
		Program program = Context.getProgramWorkflowService().getProgramByName("TUBERCULOSIS PROGRAM");
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2007, 0, 1); 
		Date enrolledOnOrAfter = calendar.getTime();
		calendar.set(2007, 0, 31);
		Date enrolledOnOrBefore = calendar.getTime();
		
		List<Program> programs = new ArrayList<Program>();
		programs.add(program);
		
		Cohort cohort = dao.getPatientsHavingProgramEnrollment(programs, null, null, null, null);
		log.warn("# Patients ever started TUBERCULOSIS PROGRAM: " + cohort.size());

		cohort = dao.getPatientsHavingProgramEnrollment(programs, null, null, calendar.getTime(), null);
		log.warn("# Patients completed TUBERCULOSIS PROGRAM on or after date: " + cohort.size());

		cohort = dao.getPatientsHavingProgramEnrollment(programs, null, null, enrolledOnOrAfter, enrolledOnOrBefore);
		log.warn("# Patients completed TUBERCULOSIS PROGRAM during period: " + cohort.size());
	}			

	
	@Test
	public void shouldGetAllPatientsStartedArvsBetweenDates() throws Exception {		
		Program program = Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM");
		ProgramWorkflow workflow = program.getWorkflowByName("TREATMENT STATUS");
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		states.add(workflow.getStateByName("ON ANTIRETROVIRALS"));
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2007, 0, 1); 
		
		Cohort cohort = dao.getPatientsHavingStates(states, null, null, null, null);
		log.warn("# Patients ON ARVs ever: " + cohort.size());

		cohort = dao.getPatientsHavingStates(states, calendar.getTime(), null, null, null);
		log.warn("# Patients ON ARVs since date: " + cohort.size());

		cohort = dao.getPatientsHavingStates(states, calendar.getTime(), new Date(), null, null);
		log.warn("# Patients ON ARVs during period: " + cohort.size());

	}

	@Test
	public void shouldGetAllPatientsDefaultedBetweenDates() throws Exception {		

		Program program = Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM");
		ProgramWorkflow workflow = program.getWorkflowByName("TREATMENT STATUS");
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		states.add(workflow.getStateByName("DEFAULTED"));
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2007, 0, 1); 
		
		List<Program> programs = new ArrayList<Program>();
		programs.add(program);
				
		Cohort cohort = dao.getPatientsHavingStates(states, null, null, null, null);
		log.warn("# Patients Defaulted ever: " + cohort.size());
		
		cohort = dao.getPatientsHavingStates(states, calendar.getTime(), null, null, null);
		log.warn("# Patients Defaulted since date: " + cohort.size());
		
		cohort = dao.getPatientsHavingStates(states, calendar.getTime(), new Date(), null, null);
		log.warn("# Patients Defaulted during period: " + cohort.size());
	}		
	
	
	
	/**
	 * @throws Exception
	 */
	@Test
	public void shouldGetAllPatientsStartedDrugsBetweenDates() throws Exception {		
		List<Drug> drugs = new ArrayList<Drug>();
		drugs.addAll(Context.getConceptService().getAllDrugs());
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2007, 0, 1); 

		Cohort cohort = dao.getPatientsHavingStartedDrugOrders(drugs, null, null);
		log.warn("# patients started any drugs ever " + cohort.size());

		cohort = dao.getPatientsHavingStartedDrugOrders(drugs, calendar.getTime(), null);
		log.warn("# patients started any drugs since date " + cohort.size());

		cohort = dao.getPatientsHavingStartedDrugOrders(drugs, calendar.getTime(), new Date());
		log.warn("# patients started any drugs during period " + cohort.size());
	}		
	
	/**
	 * @throws Exception
	 */
	@Test
	public void shouldGetAllPatientsCompletedDrugsBetweenDates() throws Exception {		
		List<Drug> drugs = new ArrayList<Drug>();
		drugs.addAll(Context.getConceptService().getAllDrugs());
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2007, 0, 1); 
		
		Cohort cohort = dao.getPatientsHavingCompletedDrugOrders(drugs, null, null);
		log.warn("# patients completed any drugs ever " + cohort.size());

		cohort = dao.getPatientsHavingCompletedDrugOrders(drugs, calendar.getTime(), null);
		log.warn("# patients completed any drugs since date " + cohort.size());

		cohort = dao.getPatientsHavingCompletedDrugOrders(drugs, calendar.getTime(), new Date());
		log.warn("# patients completed any drugs during period " + cohort.size());
	}			
	

	/**
	 * @throws Exception
	 */
	@Test
	public void shouldGetAllPatientsBornBetweenDates() throws Exception {		
		List<Drug> drugs = new ArrayList<Drug>();
		drugs.addAll(Context.getConceptService().getAllDrugs());
		
		Calendar start = Calendar.getInstance();
		start.set(2007, 0, 1); 
		
		Calendar end =  Calendar.getInstance();
		end.set(2007, 0, 31);
		
		Cohort cohort = dao.getPatientsHavingBirthDateBetweenDates(null, null);
		log.warn("# patients born ever " + cohort.size());

		cohort = dao.getPatientsHavingBirthDateBetweenDates(start.getTime(), null);
		log.warn("# patients completed any drugs since date " + cohort.size());

		cohort = dao.getPatientsHavingBirthDateBetweenDates(start.getTime(), end.getTime());
		log.warn("# patients completed any drugs during period " + cohort.size());

	}			

	/**
	 * @throws Exception
	 */
	@Test
	public void shouldGetAllPatientsDiedBetweenDates() throws Exception {		
		List<Drug> drugs = new ArrayList<Drug>();
		drugs.addAll(Context.getConceptService().getAllDrugs());
		
		Calendar start = Calendar.getInstance();
		start.set(2007, 0, 1); 
		Calendar end =  Calendar.getInstance();
		end.set(2007, 0, 31);
		
		Cohort cohort = dao.getPatientsHavingDiedBetweenDates(null, null);
		log.warn("# patients that have died ever " + cohort.size());

		cohort = dao.getPatientsHavingDiedBetweenDates(start.getTime(), null);
		log.warn("# patients that have died since date " + cohort.size());

		cohort = dao.getPatientsHavingDiedBetweenDates(start.getTime(), end.getTime());
		log.warn("# patients that have died between dates " + cohort.size());

	}			
	
	
	
}
