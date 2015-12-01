package org.openmrs.module.reporting.cohort.query.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.Drug;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.api.impl.PatientSetServiceImpl;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.result.Result;
import org.openmrs.module.reporting.cohort.Cohorts;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.cohort.query.db.CohortQueryDAO;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CohortQueryServiceImpl  extends BaseOpenmrsService implements CohortQueryService {

    protected final Log log = LogFactory.getLog(getClass());

    protected CohortQueryDAO dao;
    
    public void setCohortQueryDAO(CohortQueryDAO dao) {
        this.dao = dao;
    }

	public Cohort getPatientsWithGender(boolean includeMales, boolean includeFemales, boolean includeUnknownGender) {
		return dao.getPatientsWithGender(includeMales, includeFemales, includeUnknownGender);
	}

	public Cohort getPatientsWithAgeRange(Integer minAge, DurationUnit minAgeUnit, Integer maxAge, DurationUnit maxAgeUnit, boolean unknownAgeIncluded, Date effectiveDate) {
    	return dao.getPatientsWithAgeRange(minAge, minAgeUnit, maxAge, maxAgeUnit, unknownAgeIncluded, effectiveDate);
    }
    
	public Cohort getPatientsHavingObs(Integer conceptId, TimeModifier timeModifier,
            PatientSetServiceImpl.Modifier modifier, Object value, Date fromDate, Date toDate, List<User> providers, EncounterType encounterType) { 
		return dao.getPatientsHavingObs(conceptId, timeModifier, modifier, value, fromDate, toDate, providers, encounterType);
	}

    
    /**
     * @see CohortQueryService#getPatientsHavingProgramEnrollment(List, Date, Date, Date, Date)
     */
    public Cohort getPatientsHavingProgramEnrollment(List<Program> programs, Date enrolledOnOrAfter, Date enrolledOnOrBefore, Date completedOnOrAfter, Date completedOnOrBefore) {
		ProgramEnrollmentCohortDefinition cd = new ProgramEnrollmentCohortDefinition();
		cd.setPrograms(programs);
		cd.setEnrolledOnOrAfter(enrolledOnOrAfter);
		cd.setEnrolledOnOrBefore(enrolledOnOrBefore);
		cd.setCompletedOnOrAfter(completedOnOrAfter);
		cd.setCompletedOnOrBefore(completedOnOrBefore);
		return evaluate(cd);
     }

    public Cohort getPatientsInProgram(List<Program> programs, Date onOrAfter, Date onOrBefore) {
	    return dao.getPatientsInProgram(programs, onOrAfter, onOrBefore);
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

	/**
	 * @see org.openmrs.module.reporting.cohort.query.service.CohortQueryService#getPatientsHavingStates(java.util.List, java.util.Date, java.util.Date, java.util.Date, java.util.Date)
	 */
	public Cohort getPatientsHavingStates(List<ProgramWorkflowState> states, Date startedOnOrAfter, Date startedOnOrBefore,
                                          Date endedOnOrAfter, Date endedOnOrBefore) {
		return dao.getPatientsHavingStates(states, startedOnOrAfter, startedOnOrBefore, endedOnOrAfter, endedOnOrBefore);
    }

	/**
	 * @see org.openmrs.module.reporting.cohort.query.service.CohortQueryService#getPatientsInStates(java.util.List, java.util.Date, java.util.Date)
	 */
	public Cohort getPatientsInStates(List<ProgramWorkflowState> states, Date onOrAfter, Date onOrBefore) {
		return dao.getPatientsInStates(states, onOrAfter, onOrBefore);
    }

	/**
	 * @see org.openmrs.module.reporting.cohort.query.service.CohortQueryService#getPatientsHavingRangedObs(org.openmrs.api.PatientSetService.TimeModifier, org.openmrs.Concept, org.openmrs.Concept, java.util.Date, java.util.Date, java.util.List, java.util.List, org.openmrs.module.reporting.common.RangeComparator, java.lang.Object, org.openmrs.module.reporting.common.RangeComparator, java.lang.Object)
	 */
	public Cohort getPatientsHavingRangedObs(TimeModifier timeModifier, Concept question, Concept groupingConcept,
                                              Date onOrAfter, Date onOrBefore, List<Location> locationList,
                                              List<EncounterType> encounterTypeList, RangeComparator operator1, Object value1,
                                              RangeComparator operator2, Object value2) {
	    return dao.getPatientsHavingRangedObs(
	    	timeModifier, question, groupingConcept,
	    	onOrAfter, onOrBefore,
	    	locationList, encounterTypeList,
	    	operator1, value1,
	    	operator2, value2);
    }

	/**
	 * @see org.openmrs.module.reporting.cohort.query.service.CohortQueryService#getPatientsHavingEncounters(java.util.Date, java.util.Date, java.util.List, java.util.List, java.util.List, java.lang.Integer, java.lang.Integer)
	 */
	public Cohort getPatientsHavingEncounters(Date onOrAfter, Date onOrBefore,
	                                          List<Location> locationList, List<EncounterType> encounterTypeList, List<Form> formList,
                                              Integer atLeastCount, Integer atMostCount) {
	    return getPatientsHavingEncounters(onOrAfter, onOrBefore, locationList, encounterTypeList, formList, atLeastCount, atMostCount);
    }

	public Cohort getPatientsHavingEncounters(Date onOrAfter, Date onOrBefore, TimeQualifier timeQualifier, 
	                                          List<Location> locationList, List<EncounterType> encounterTypeList, List<Form> formList,
                                              Integer atLeastCount, Integer atMostCount, User createdBy, Date createdOnOrAfter, Date createdOnOrBefore) {
	    return getPatientsHavingEncounters(onOrAfter, onOrBefore, timeQualifier, locationList, null, encounterTypeList, formList, 
	    								   atLeastCount, atMostCount, createdBy, createdOnOrAfter, createdOnOrBefore);
    }

	public Cohort getPatientsHavingEncounters(Date onOrAfter, Date onOrBefore, TimeQualifier timeQualifier, List<Location> locationList, 
	                                          List<Person> providerList, List<EncounterType> encounterTypeList, List<Form> formList,
                                              Integer atLeastCount, Integer atMostCount, User createdBy, Date createdOnOrAfter, Date createdOnOrBefore) {
	    return dao.getPatientsHavingEncounters(onOrAfter, onOrBefore, timeQualifier, locationList, providerList, encounterTypeList, formList,
	    									   atLeastCount, atMostCount, createdBy, createdOnOrAfter, createdOnOrBefore);
    }

	/**
	 * @see org.openmrs.module.reporting.cohort.query.service.CohortQueryService#getPatientsHavingDiscreteObs(org.openmrs.api.PatientSetService.TimeModifier, org.openmrs.Concept, org.openmrs.Concept, java.util.Date, java.util.Date, java.util.List, java.util.List, org.openmrs.module.reporting.common.SetComparator, java.util.List)
	 */
	public Cohort getPatientsHavingDiscreteObs(TimeModifier timeModifier, Concept question, Concept groupingConcept,
                                               Date onOrAfter, Date onOrBefore, List<Location> locationList,
                                               List<EncounterType> encounterTypeList, SetComparator operator,
                                               List<? extends Object> valueList) {
		return dao.getPatientsHavingDiscreteObs(
	    	timeModifier, question, groupingConcept,
	    	onOrAfter, onOrBefore,
	    	locationList, encounterTypeList,
	    	operator, valueList);
    }

	/**
	 * @see org.openmrs.module.reporting.cohort.query.service.CohortQueryService#getPatientsHavingBirthAndDeath(java.util.Date, java.util.Date, java.util.Date, java.util.Date)
	 */
	public Cohort getPatientsHavingBirthAndDeath(Date bornOnOrAfter, Date bornOnOrBefore,
	                                             Date diedOnOrAfter, Date diedOnOrBefore) {
	    return dao.getPatientsHavingBirthAndDeath(bornOnOrAfter, bornOnOrBefore, diedOnOrAfter, diedOnOrBefore);
    }
    
	/**
	 * @see org.openmrs.module.reporting.cohort.query.service.CohortQueryService#getPatientsHavingPersonAttributes(org.openmrs.PersonAttributeType, java.util.List)
	 */
	public Cohort getPatientsHavingPersonAttributes(PersonAttributeType attribute, List<String> values) {
		return dao.getPatientsHavingPersonAttributes(attribute, values);
	}

	/**
	 * @see org.openmrs.module.reporting.cohort.query.service.CohortQueryService#executeSqlQuery(java.lang.String, java.util.Map)
	 */
	public Cohort executeSqlQuery(String sqlQuery, Map<String,Object> paramMap) {
		return dao.executeSqlQuery(sqlQuery, paramMap);
	}
	
	/**
	 * @see org.openmrs.module.reporting.cohort.query.service.CohortQueryService#executeLogicQuery(java.lang.String, java.util.Map, org.openmrs.Cohort)
	 */
	public Cohort executeLogicQuery(String logicExpression, Map<String, Object> parameters, Cohort baseCohort) {
		if (baseCohort == null) {
            baseCohort = Cohorts.allPatients(null);
        }

	    LogicService logicService = Context.getLogicService();
	    /*
	     * The following code should read:
	     *     LogicCriteria lc = logicService.parse(logicExpression);
	     *     Map<Integer, Result> results = logicService.eval(baseCohort, lc, parameters);
	     * But I have to write it in this hacky way for 1.5.x compatibility, because
	     *  * LogicService.parse wasn't introduced until 1.6
	     *  * LogicCriteria is a class in 1.5.x and an interface in 1.6+
	     * If we ever stop supporting 1.5.x in the reporting module, remove the hack below
	     */
	    Object logicCriteria = logicService.parseString(logicExpression);
	    Method evalMethod = findLogicEvalMethodForCohortLogicCriteriaAndParams();
	    Map<Integer, Result> results = null;
	    try {
	    	results = (Map<Integer, Result>) evalMethod.invoke(logicService, baseCohort, logicCriteria, parameters);
	    } catch (Exception ex) {
	    	throw new RuntimeException(ex);
	    }
	    // END HACKY CODE
	    Cohort ret = new Cohort();
	    for (Map.Entry<Integer, Result> e : results.entrySet())
	    	if (e.getValue().toBoolean())
	    		ret.addMember(e.getKey());
	    return ret;
	}
	
	/**
	 * Helper because I need to avoid explicitly naming the LogicCriteria class, since it breaks things to
	 * compile against that (class) in 1.5, since it becomes an interface in 1.6
     * @return the LogicService.eval(Cohort, LogicCriteria, Map) method
     */
    private Method findLogicEvalMethodForCohortLogicCriteriaAndParams() {
	    for (Method m : LogicService.class.getMethods()) {
	    	Class<?>[] paramTypes = m.getParameterTypes();
	    	if (paramTypes.length != 3)
	    		continue;
	    	if (!paramTypes[0].equals(Cohort.class))
	    		continue;
	    	if (!paramTypes[2].equals(Map.class))
	    		continue;
	    	if (!paramTypes[1].getName().equals("org.openmrs.logic.LogicCriteria"))
	    		continue;
	    	return m;
	    }
	    throw new RuntimeException("couldn't find LogicService.eval(Cohort, LogicCriteria, Map) method");
    }

	/**
	 * @see org.openmrs.module.reporting.cohort.query.service.CohortQueryService#getNamedParameters(java.lang.String)
	 */
	public List<Parameter> getNamedParameters(String sqlQuery) { 
		return dao.getNamedParameters(sqlQuery);
	}

	private Cohort evaluate(CohortDefinition cohortDefinition) {
		try {
			CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
			EvaluatedCohort c = cds.evaluate(cohortDefinition, new EvaluationContext());
			return new Cohort(c.getMemberIds());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
