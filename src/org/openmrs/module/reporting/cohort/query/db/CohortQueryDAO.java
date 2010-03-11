package org.openmrs.module.reporting.cohort.query.db;

import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Drug;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.impl.PatientSetServiceImpl;
import org.openmrs.module.reporting.common.DurationUnit;

public interface CohortQueryDAO {
	
	//public Cohort getPatientsInPrograms(List<Program> programs, Date startedAfter, Date startedBefore);
	
	public Cohort getPatientsWithGender(boolean includeMales, boolean includeFemales, boolean includeUnknownGender);
	public Cohort getPatientsWithAgeRange(Integer minAge, DurationUnit minAgeUnit, Integer maxAge, DurationUnit maxAgeUnit, boolean unknownAgeIncluded, Date effectiveDate);
	
    // Started or stopped programs in specified periods
	public Cohort getPatientsHavingProgramEnrollment(List<Program> programs, Date enrolledOnOrAfter, Date enrolledOnOrBefore, Date completedOnOrAfter, Date completedOnOrBefore);
        
    // Started or stopped drugs during a period
    public Cohort getPatientsHavingActiveDrugOrders(List<Drug> drugs, Date asOfDate);
    public Cohort getPatientsHavingStartedDrugOrders(List<Drug> drugs, Date startedOnOrAfter, Date startedOnOrBefore);
    public Cohort getPatientsHavingCompletedDrugOrders(List<Drug> drugs, Date completedOnOrAfter, Date completedOnOrBefore);

    // Born or died between dates
    public Cohort getPatientsHavingBirthDateBetweenDates(Date onOrAfter, Date onOrBefore);
    public Cohort getPatientsHavingDiedBetweenDates(Date onOrAfter, Date onOrBefore);

    // Patients having certain observations 
	public Cohort getPatientsHavingObs(Integer conceptId, TimeModifier timeModifier,
            PatientSetServiceImpl.Modifier modifier, Object value, Date fromDate, Date toDate, List<User> providers, EncounterType encounterType);
	
	// Patients who were in a Program on the specified date or range
	public Cohort getPatientsInProgram(List<Program> programs, Date onOrAfter, Date onOrBefore);

	// based on ranges of start and end dates for patient_state
	public Cohort getPatientsHavingStates(List<ProgramWorkflowState> states, Date startedOnOrAfter, Date startedOnOrBefore, Date endedOnOrAfter, Date endedOnOrBefore);

	// Patients who were in a State in the specified date range
	public Cohort getPatientsInStates(List<ProgramWorkflowState> states, Date onOrAfter, Date onOrBefore);

}
