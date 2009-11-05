package org.openmrs.module.cohort.query.db;

import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Drug;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.impl.PatientSetServiceImpl;

public interface CohortQueryDAO {
	
	//public Cohort getPatientsInPrograms(List<Program> programs, Date startedAfter, Date startedBefore);
	
	
    // Started or stopped programs during a period
	public Cohort getPatientsHavingStartedPrograms(List<Program> programs, Date startedOnOrAfter, Date startedOnOrBefore);
    public Cohort getPatientsHavingCompletedPrograms(List<Program> programs, Date completedOnOrAfter, Date completedOnOrBefore);
    
    // Started or stopped program states during a period
    public Cohort getPatientsHavingStartedStates(List<ProgramWorkflowState> states, Date startedOnOrAfter, Date startedOnOrBefore);
    public Cohort getPatientsHavingCompletedStates(List<ProgramWorkflowState> states, Date completedOnOrAfter, Date completedOnOrBefore);    
    
    // Started or stopped drugs during a period
    public Cohort getPatientsHavingActiveDrugOrders(List<Drug> drugs, Date asOfDate);
    public Cohort getPatientsHavingStartedDrugOrders(List<Drug> drugs, Date startedOnOrAfter, Date startedOnOrBefore);
    public Cohort getPatientsHavingCompletedDrugOrders(List<Drug> drugs, Date completedOnOrAfter, Date completedOnOrBefore);

    // Born or died between dates
    public Cohort getPatientsHavingBirthDateBetweenDates(Date onOrAfter, Date onOrBefore);
    public Cohort getPatientsHavingDiedBetweenDates(Date onOrAfter, Date onOrBefore);

    // Patients having certain observations 
	public Cohort getPatientsHavingObs(Integer conceptId, TimeModifier timeModifier,
            PatientSetServiceImpl.Modifier modifier, Object value, Date fromDate, Date toDate, List<User> providers);

}
