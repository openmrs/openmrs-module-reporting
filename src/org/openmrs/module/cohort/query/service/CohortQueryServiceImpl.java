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
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.api.impl.PatientSetServiceImpl;
import org.openmrs.module.cohort.query.db.CohortQueryDAO;

public class CohortQueryServiceImpl  extends BaseOpenmrsService implements CohortQueryService {

    protected final Log log = LogFactory.getLog(getClass());

    protected CohortQueryDAO dao;
    
    public void setCohortQueryDAO(CohortQueryDAO dao) {
        this.dao = dao;
    }
    
    
	public Cohort getPatientsHavingObs(Integer conceptId, TimeModifier timeModifier,
            PatientSetServiceImpl.Modifier modifier, Object value, Date fromDate, Date toDate, List<User> providers) { 
		return dao.getPatientsHavingObs(conceptId, timeModifier, modifier, value, fromDate, toDate, providers);
	}

    
    public Cohort getPatientsHavingStartedPrograms(List<Program> programs, Date startedOnOrAfter, Date startedOnOrBefore) {     	
    	return dao.getPatientsHavingStartedPrograms(programs, startedOnOrAfter, startedOnOrBefore);
    }
    public Cohort getPatientsHavingCompletedPrograms(List<Program> programs, Date completedOnOrAfter, Date completedOnOrBefore) {     	
    	return dao.getPatientsHavingCompletedPrograms(programs, completedOnOrAfter, completedOnOrBefore);
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
