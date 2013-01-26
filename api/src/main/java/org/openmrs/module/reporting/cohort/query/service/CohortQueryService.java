package org.openmrs.module.reporting.cohort.query.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.impl.PatientSetServiceImpl;
import org.openmrs.module.reporting.cohort.query.db.CohortQueryDAO;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.transaction.annotation.Transactional;

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

	public Cohort getPatientsHavingObs(Integer conceptId, TimeModifier timeModifier,
            PatientSetServiceImpl.Modifier modifier, Object value, Date fromDate, Date toDate, List<User> providers, EncounterType encounterType);

	/**
	 * Get patients having ranged (i.e. Numeric or Date/Time) obs that match a complicated query.
	 * 
	 * @param timeModifier
	 * @param question
	 * @param groupingConcept
	 * @param onOrAfter
	 * @param onOrBefore
	 * @param locationList
	 * @param encounterTypeList
	 * @param operator1
	 * @param value1 if non-null this value controls whether the query looks at value_numeric or value_datetime
	 * @param operator2
	 * @param value2
	 * @return cohort of patients with matching obs
	 * 
	 * @should get patients with any obs of a specified concept
	 * @should get patients whose first obs of a specified concept is in a range
	 * @should get patients whose maximum obs of a specified concept is equal to a specified value
	 * @should get patients with any obs of a specified concept in a specified encounter type
	 * @should get patients whose first obs of a specified concept in a specified encounter type is in a range
	 * @should get patients whose maximum obs of a specified concept in a specified encounter type is equals to a specified value
	 * @should get patients with a query with all parameters
	 */
	public Cohort getPatientsHavingRangedObs(TimeModifier timeModifier, Concept question, Concept groupingConcept,
                                              Date onOrAfter, Date onOrBefore, List<Location> locationList,
                                              List<EncounterType> encounterTypeList, RangeComparator operator1, Object value1,
                                              RangeComparator operator2, Object value2);
	
	/**
	 * Get patients having discrete (i.e. Text or Coded) obs that match a complicated query.
	 * 
	 * @param timeModifier
	 * @param question
	 * @param groupingConcept
	 * @param onOrAfter
	 * @param onOrBefore
	 * @param locationList
	 * @param encounterTypeList
	 * @param operator
	 * @param valueList if non-null the first value in this list controls whether the query looks at value_text or value_coded
	 * @return cohort of patients with matching obs
	 */
	public Cohort getPatientsHavingDiscreteObs(TimeModifier timeModifier, Concept question, Concept groupingConcept,
	                                           Date onOrAfter, Date onOrBefore, List<Location> locationList,
	                                           List<EncounterType> encounterTypeList, SetComparator operator,
	                                           List<? extends Object> valueList);
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
	 * placeholders in the query.

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
     * which basically means that empty results, and the boolean value false are "failure", and anything
     * else "passes".) 
     * 
     * @param logic
     * @param parameterValues
     * @param baseCohort if not null, only look at patients in this cohort
     * @return
     */
    public Cohort executeLogicQuery(String logicExpression, Map<String, Object> parameterValues, Cohort baseCohort);

}