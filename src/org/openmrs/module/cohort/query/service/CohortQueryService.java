package org.openmrs.module.cohort.query.service;

import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.impl.PatientSetServiceImpl;
import org.openmrs.module.cohort.query.db.CohortQueryDAO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CohortQueryService extends OpenmrsService {
	public void setCohortQueryDAO(CohortQueryDAO dao);	
	
    public Cohort getPatientsHavingStartedPrograms(List<Program> programs, Date startedOnOrAfter, Date startedOnOrBefore);
    public Cohort getPatientsHavingCompletedPrograms(List<Program> programs, Date completedOnOrAfter, Date completedOnOrBefore);
    public Cohort getPatientsHavingStartedStates(List<ProgramWorkflowState> states, Date startedOnOrAfter, Date startedOnOrBefore);
    public Cohort getPatientsHavingCompletedStates(List<ProgramWorkflowState> states, Date completedOnOrAfter, Date completedOnOrBefore);
    
    public Cohort getPatientsHavingActiveDrugOrders(List<Drug> drugs, Date asOfDate);
    public Cohort getPatientsHavingStartedDrugOrders(List<Drug> drugs, List<Concept> drugSetConcepts, Date startedOnOrAfter, Date startedOnOrBefore);
    public Cohort getPatientsHavingCompletedDrugOrders(List<Drug> drugs, List<Concept> drugSetConcepts, Date completedOnOrAfter, Date completedOnOrBefore);

	public Cohort getPatientsHavingObs(Integer conceptId, TimeModifier timeModifier,
            PatientSetServiceImpl.Modifier modifier, Object value, Date fromDate, Date toDate, List<User> providers, EncounterType encounterType);


}