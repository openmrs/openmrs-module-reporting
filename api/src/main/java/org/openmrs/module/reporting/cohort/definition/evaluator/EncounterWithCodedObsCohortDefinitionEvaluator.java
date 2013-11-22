package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.OpenmrsObject;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterWithCodedObsCohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Handler(supports = EncounterWithCodedObsCohortDefinition.class)
public class EncounterWithCodedObsCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
        EncounterWithCodedObsCohortDefinition cd = (EncounterWithCodedObsCohortDefinition) cohortDefinition;

        String sql = "select distinct e.patient_id \n" +
                "from encounter e \n" +
                (cd.isIncludeNoObsValue() ? "left outer" : "inner") + " join obs o \n" +
                "  on e.encounter_id = o.encounter_id \n" +
                "  and o.voided = false \n";
        if (cd.getConcept() != null) {
            sql += "  and o.concept_id = :concept \n";
        }
        sql += "where e.voided = false \n";
        if (cd.getEncounterTypeList() != null) {
            sql += "  and e.encounter_type in (:encounterTypeList) \n";
        }
        if (cd.getLocationList() != null) {
            sql += "  and e.location_id in (:locationList) \n";
        }
        sql += valueCodedClause(cd.isIncludeNoObsValue(), cd.getIncludeCodedValues(), cd.getExcludeCodedValues());
        if (cd.getOnOrAfter() != null) {
            sql += "  and e.encounter_datetime >= :onOrAfter \n";
        }
        if (cd.getOnOrBefore() != null) {
            sql += "  and e.encounter_datetime <= :onOrBefore \n";
        }

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        if (cd.getConcept() != null) {
            query.setInteger("concept", cd.getConcept().getId());
        }
        if (cd.getEncounterTypeList() != null) {
            query.setParameterList("encounterTypeList", idList(cd.getEncounterTypeList()));
        }
        if (cd.getLocationList() != null) {
            query.setParameterList("locationList", idList(cd.getLocationList()));
        }
        if (cd.getOnOrAfter() != null) {
            query.setTimestamp("onOrAfter", cd.getOnOrAfter());
        }
        if (cd.getOnOrBefore() != null) {
            query.setTimestamp("onOrBefore", DateUtil.getEndOfDayIfTimeExcluded(cd.getOnOrBefore()));
        }
        if (cd.getIncludeCodedValues() != null) {
            query.setParameterList("includeCodedValues", cd.getIncludeCodedValues());
        }
        if (cd.getExcludeCodedValues() != null) {
            query.setParameterList("excludeCodedValues", cd.getExcludeCodedValues());
        }

        Cohort c = new Cohort();
        for (Integer i : (List<Integer>) query.list()){
            c.addMember(i);
        }
        return new EvaluatedCohort(c, cohortDefinition, context);
    }

    private List<Integer> idList(List<? extends OpenmrsObject> in) {
        List<Integer> ids = new ArrayList<Integer>(in.size());
        for (OpenmrsObject o : in) {
            ids.add(o.getId());
        }
        return ids;
    }

    private String valueCodedClause(boolean includeNoObsValue, List<Concept> includeCodedValues, List<Concept> excludeCodedValues) {
        if (includeNoObsValue && includeCodedValues == null && excludeCodedValues == null) {
            return "and o.value_coded is null";
        }
        String clause = "";
        if (includeCodedValues != null) {
            if (includeNoObsValue) {
                clause += " and (o.value_coded is null or o.value_coded in (:includeCodedValues)) ";
            } else {
                clause += " and o.value_coded in (:includeCodedValues) ";
            }
        }
        if (excludeCodedValues != null) {
            if (includeNoObsValue) {
                clause += " and (o.value_coded is null or o.value_coded not in (:excludeCodedValues)) ";
            } else {
                clause += " and o.value_coded not in (:excludeCodedValues) ";
            }
        }
        return clause;
    }

}
