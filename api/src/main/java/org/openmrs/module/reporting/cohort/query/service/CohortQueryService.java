/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.query.service;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.reporting.cohort.query.db.CohortQueryDAO;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Transactional(readOnly=true)
public interface CohortQueryService extends OpenmrsService {
	
	public void setCohortQueryDAO(CohortQueryDAO dao);	
	
	public Cohort getPatientsWithGender(boolean includeMales, boolean includeFemales, boolean includeUnknownGender);
	public Cohort getPatientsWithAgeRange(Integer minAge, DurationUnit minAgeUnit, Integer maxAge, DurationUnit maxAgeUnit, boolean unknownAgeIncluded, Date effectiveDate);

	public Cohort getPatientsHavingProgramEnrollment(List<Program> programs, Date enrolledOnOrAfter, Date enrolledOnOrBefore, Date completedOnOrAfter, Date completedOnOrBefore);
	public Cohort getPatientsInProgram(List<Program> programs, Date onOrAfter, Date onOrBefore);

	public Cohort getPatientsHavingStates(List<ProgramWorkflowState> states, Date startedOnOrAfter, Date startedOnOrBefore, Date endedOnOrAfter, Date endedOnOrBefore);
	public Cohort getPatientsInStates(List<ProgramWorkflowState> states, Date onOrAfter, Date onOrBefore);
	
    public Cohort getPatientsHavingActiveDrugOrders(List<Drug> drugs, Date asOfDate);
    public Cohort getPatientsHavingStartedDrugOrders(List<Drug> drugs, List<Concept> drugSetConcepts, Date startedOnOrAfter, Date startedOnOrBefore);
    public Cohort getPatientsHavingCompletedDrugOrders(List<Drug> drugs, List<Concept> drugSetConcepts, Date completedOnOrAfter, Date completedOnOrBefore);


	/**
	 * Get patients having encounters with the following characteristics
	 * 
	 * @param onOrAfter
	 * @param onOrBefore
	 * @param locationList
	 * @param encounterTypeList
	 * @param formList
	 * @param atLeastCount
	 * @param atMostCount
	 * @return cohort of patients matching the query
	 */
	public Cohort getPatientsHavingEncounters(Date onOrAfter, Date onOrBefore, List<Location> locationList,
                                              List<EncounterType> encounterTypeList, List<Form> formList,
                                              Integer atLeastCount, Integer atMostCount);
	
	/**
	 * Get patients having encounters with the following characteristics
	 * 
	 * @param onOrAfter
	 * @param onOrBefore
	 * @param locationList
	 * @param encounterTypeList
	 * @param formList
	 * @param atLeastCount
	 * @param atMostCount
	 * @param createdBy
	 * @param createdOnOrAfter 
	 * @param createdOnOrBefore 
	 * @return cohort of patients matching the query
	 */
	public Cohort getPatientsHavingEncounters(Date onOrAfter, Date onOrBefore, TimeQualifier timeQualifier, List<Location> locationList,
                                              List<EncounterType> encounterTypeList, List<Form> formList,
                                              Integer atLeastCount, Integer atMostCount, User createdBy, Date createdOnOrAfter, Date createdOnOrBefore);
		
	/**
	 * Get patients having encounters with the following characteristics
	 * 
	 * @should should get patients having encounters with a specified provider
	 * @return cohort of patients matching the query
	 */
	public Cohort getPatientsHavingEncounters(Date onOrAfter, Date onOrBefore, TimeQualifier timeQualifier, List<Location> locationList,
                                              List<Person> providerList, List<EncounterType> encounterTypeList, List<Form> formList,
                                              Integer atLeastCount, Integer atMostCount, User createdBy, Date createdOnOrAfter, Date createdOnOrBefore);
	
	/**
	 * Get patients having person attributes of a particular type or that contain certain values.  
	 * 
	 * @should get patients with an attribute of given attribute type
	 * @should get patients with an attribute of given attribute type and containing given values
	 * @should get patients with an attribute containing given values  
	 * 
	 * @param attribute
	 * @param values
	 * @return	cohort of patients matching the query
	 */
	public Cohort getPatientsHavingPersonAttributes(PersonAttributeType attributeType, List<String> values);
		
	/**
	 * Executes the given sql query string.  This method will substitute  
	 * parameter values from the given parameter map into the named parameter
	 * placeholder in the query.

	 * TODO The query string should actually be a bean that encapsulates any
	 * type of query (SQL, HQL, MDX).  The execute query method would actually 
	 * delegate to handlers (parser, validator, executor), similar to the 
	 * way most of our reporting objects are handled by a persister, evaluator 
	 * handlers.
	 * 
	 * @should return null when given sqlQuery returns no results
	 * @should return a not-null cohort when returns results
	 * @should throw error when given sqlQuery is invalid
	 * 
	 * @param sqlQuery	a sql query string to be executed 
	 * @param paramMap 	a map of parameter values indexed by the string parameter name
	 * @return	a {@link Cohort} of patients matching the given sqlQuery
	 */
	public Cohort executeSqlQuery(String sqlQuery, Map<String,Object> paramMap); 
	
	/**
	 * Get named parameters from the given sql query.  This method will return
	 * a list of {@link Parameter}s that have been created based on any named 
	 * parameters (e.g. "patient_id = :patientId") that have been found within 
	 * the given sql query.
	 * 
	 * @param sqlQuery	the sql query string to be executed
	 * @return	a List of {@link Parameter}s 
	 */
	public List<Parameter> getNamedParameters(String sqlQuery);
	
	/**
	 * Get patients who were born or died in a particular date range
	 * 
	 * @param bornOnOrAfter
	 * @param bornOnOrBefore
	 * @param diedOnOrAfter
	 * @param diedOnOrBefore
	 * @return
	 */
	public Cohort getPatientsHavingBirthAndDeath(Date bornOnOrAfter, Date bornOnOrBefore,
	                                             Date diedOnOrAfter, Date diedOnOrBefore);

	/**
     * Returns a cohort of patients who "pass" a logic expression. (Depends on Logic's Result.toBoolean,
     * which basically means that empty results and the boolean value false are "failure", and anything
     * else "passes".) 
     * 
     * @param logicExpression
     * @param parameterValues
     * @param baseCohort if not null, only look at patients in this cohort
     * @return
     */
    public Cohort executeLogicQuery(String logicExpression, Map<String, Object> parameterValues, Cohort baseCohort);

}
