package org.openmrs.module.cohort.query.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.Drug;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.api.impl.PatientSetServiceImpl;
import org.openmrs.module.cohort.query.db.CohortQueryDAO;
import org.openmrs.module.common.DurationUnit;

public class CohortQueryServiceImpl  extends BaseOpenmrsService implements CohortQueryService {

    protected final Log log = LogFactory.getLog(getClass());

    protected CohortQueryDAO dao;
    
    public void setCohortQueryDAO(CohortQueryDAO dao) {
        this.dao = dao;
    }
    
    public Cohort getPatientsWithAgeRange(Integer minAge, DurationUnit minAgeUnit, Integer maxAge, DurationUnit maxAgeUnit, boolean unknownAgeIncluded, Date effectiveDate) {
    	return dao.getPatientsWithAgeRange(minAge, minAgeUnit, maxAge, maxAgeUnit, unknownAgeIncluded, effectiveDate);
    }
    
	public Cohort getPatientsHavingObs(Integer conceptId, TimeModifier timeModifier,
            PatientSetServiceImpl.Modifier modifier, Object value, Date fromDate, Date toDate, List<User> providers, EncounterType encounterType) { 
		return dao.getPatientsHavingObs(conceptId, timeModifier, modifier, value, fromDate, toDate, providers, encounterType);
	}

    
    /**
     * @see org.openmrs.module.cohort.query.service.CohortQueryService#getPatientsHavingProgramEnrollment(java.util.List, java.util.Date, java.util.Date, java.util.Date, java.util.Date)
     */
    public Cohort getPatientsHavingProgramEnrollment(List<Program> programs, Date enrolledOnOrAfter, Date enrolledOnOrBefore,
                                                     Date completedOnOrAfter, Date completedOnOrBefore) {
    	return dao.getPatientsHavingProgramEnrollment(programs, enrolledOnOrAfter, enrolledOnOrBefore, completedOnOrAfter, completedOnOrBefore);
    }


	/**
     * @see org.openmrs.module.cohort.query.service.CohortQueryService#getPatientsInProgram(java.util.List, java.util.Date, java.util.Date, java.util.Date)
     */
    public Cohort getPatientsInProgram(List<Program> programs, Date onOrAfter, Date onOrBefore) {
	    return dao.getPatientsInProgram(programs, onOrAfter, onOrBefore);
    }


	public Cohort getPatientsHavingStartedPrograms(List<Program> programs, Date startedOnOrAfter, Date startedOnOrBefore) {     	
    	return dao.getPatientsHavingProgramEnrollment(programs, startedOnOrAfter, startedOnOrBefore, null, null);
    }
    public Cohort getPatientsHavingCompletedPrograms(List<Program> programs, Date completedOnOrAfter, Date completedOnOrBefore) {     	
    	return dao.getPatientsHavingProgramEnrollment(programs, null, null, completedOnOrAfter, completedOnOrBefore);
    }
    
    public Cohort getPatientsHavingStartedStates(List<ProgramWorkflowState> states, Date startedOnOrAfter, Date startedOnOrBefore) {     	
    	return dao.getPatientsHavingStartedStates(states, startedOnOrAfter, startedOnOrBefore);
    }
    public Cohort getPatientsHavingCompletedStates(List<ProgramWorkflowState> states, Date completedOnOrAfter, Date completedOnOrBefore) {     	
    	return dao.getPatientsHavingCompletedStates(states, completedOnOrAfter, completedOnOrBefore);
    }

    
    public Cohort getPatientsHavingActiveDrugOrders(List<Drug> drugs, Date asOfDate) { 
    	return dao.getPatientsHavingActiveDrugOrders(drugs, asOfDate);
    }
    
    
    public Cohort getPatientsHavingStartedDrugOrders(List<Drug> drugs, List<Concept> drugSetConcepts, Date startedOnOrAfter, Date startedOnOrBefore) {     	
    	if (drugSetConcepts != null) {
    		for(Concept drugSetConcept : drugSetConcepts) { 
    			drugs.addAll(getDrugsByConceptSet(drugSetConcept));
    		}
    	}
    	return dao.getPatientsHavingStartedDrugOrders(drugs, startedOnOrAfter, startedOnOrBefore);
    }
    
    public Cohort getPatientsHavingCompletedDrugOrders(List<Drug> drugs, List<Concept> drugSetConcepts, Date completedOnOrAfter, Date completedOnOrBefore) {     	
    	if (drugSetConcepts != null) {
    		for(Concept drugSetConcept : drugSetConcepts) { 
    			drugs.addAll(getDrugsByConceptSet(drugSetConcept));
    		}
    	}
    	return dao.getPatientsHavingCompletedDrugOrders(drugs, completedOnOrAfter, completedOnOrBefore);
    }
    
    public List<Drug> getDrugsByConceptSet(Concept drugSetConcept) {    
    	List<Drug> drugs = new ArrayList<Drug>();
		List<ConceptSet> drugSets = Context.getConceptService().getConceptSetsByConcept(drugSetConcept);				
	    if (drugSets != null) {
	    	for (ConceptSet drugSet : drugSets) {
	    		List<Drug> otherDrugs = Context.getConceptService().getDrugsByConcept(drugSet.getConcept());
	    		drugs.addAll(otherDrugs);
			}
    	}		    	
	    return drugs;
    }


	
    
}
