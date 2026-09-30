/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.query.db;

import org.openmrs.Cohort;
import org.openmrs.Drug;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CohortQueryDAO {
	
	//public Cohort getPatientsInPrograms(List<Program> programs, Date startedAfter, Date startedBefore);
	
	public Cohort getPatientsWithGender(boolean includeMales, boolean includeFemales, boolean includeUnknownGender);
	public Cohort getPatientsWithAgeRange(Integer minAge, DurationUnit minAgeUnit, Integer maxAge, DurationUnit maxAgeUnit, boolean unknownAgeIncluded, Date effectiveDate);

    // Started or stopped drugs during a period
    public Cohort getPatientsHavingActiveDrugOrders(List<Drug> drugs, Date asOfDate);
    public Cohort getPatientsHavingStartedDrugOrders(List<Drug> drugs, Date startedOnOrAfter, Date startedOnOrBefore);
    public Cohort getPatientsHavingCompletedDrugOrders(List<Drug> drugs, Date completedOnOrAfter, Date completedOnOrBefore);

    // Born or died between dates
    public Cohort getPatientsHavingBirthDateBetweenDates(Date onOrAfter, Date onOrBefore);
    public Cohort getPatientsHavingDiedBetweenDates(Date onOrAfter, Date onOrBefore);
	
	// Patients who were in a Program on the specified date or range
	public Cohort getPatientsInProgram(List<Program> programs, Date onOrAfter, Date onOrBefore);

	// Based on ranges of start and end dates for patient_state
	public Cohort getPatientsHavingStates(List<ProgramWorkflowState> states, Date startedOnOrAfter, Date startedOnOrBefore, Date endedOnOrAfter, Date endedOnOrBefore);

	// Patients who were in a State in the specified date range
	public Cohort getPatientsInStates(List<ProgramWorkflowState> states, Date onOrAfter, Date onOrBefore);

	// Patients having encounters matching a query
	public Cohort getPatientsHavingEncounters(Date onOrAfter, Date onOrBefore, TimeQualifier timeQualifier, List<Location> locationList,
                                              List<Person> providerList, List<EncounterType> encounterTypeList, List<Form> formList,
                                              Integer atLeastCount, Integer atMostCount, User createdBy, Date createdOnOrAfter, Date createdOnOrBefore);

	// Patients based on birth and death dates
	public Cohort getPatientsHavingBirthAndDeath(Date bornOnOrAfter, Date bornOnOrBefore,
	                                             Date diedOnOrAfter, Date diedOnOrBefore);

	// Patients having person attributes with given attribute and/or containing
	// given values
	
	/**
	 * 
	 */
	public Cohort getPatientsHavingPersonAttributes(PersonAttributeType attribute, List<String> values);

	public Cohort executeSqlQuery(String sqlQuery, Map<String,Object> paramMap);
	
	public List<Parameter> getNamedParameters(String sqlQuery);

}
