package org.openmrs.module.cohort.query.db;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.openmrs.Cohort;
import org.openmrs.ConceptName;
import org.openmrs.Drug;
import org.openmrs.Order;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.db.DAOException;

public interface CohortQueryDAO {
	
	//public Cohort getPatientsInPrograms(List<Program> programs, Date startedAfter, Date startedBefore);
	
    // Started or stopped programs during a period
	public Cohort getPatientsHavingStartedPrograms(List<Program> programs, Date startedOnOrAfter, Date startedOnOrBefore);
    public Cohort getPatientsHavingCompletedPrograms(List<Program> programs, Date completedOnOrAfter, Date completedOnOrBefore);
    
    // Started or stopped program states during a period
    public Cohort getPatientsHavingStartedStates(List<ProgramWorkflowState> states, Date stoppedOnOrAfter, Date stoppedOnOrBefore);
    public Cohort getPatientsHavingCompletedStates(List<ProgramWorkflowState> states, Date stoppedOnOrAfter, Date stoppedOnOrBefore);    
    
    // Started or stopped drugs during a period
    public Cohort getPatientsHavingStartedDrugs(List<Drug> drugs, Date startedOnOrAfter, Date startedOnOrBefore);
    public Cohort getPatientsHavingCompletedDrugs(List<Drug> drugs, Date startedOnOrAfter, Date startedOnOrBefore);
}
