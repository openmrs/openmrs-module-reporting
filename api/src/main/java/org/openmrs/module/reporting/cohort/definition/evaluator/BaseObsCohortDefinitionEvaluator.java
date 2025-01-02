/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.Cohorts;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Provides base queries shared by various evaluator subclasses for each ObsCohortDefinition
 * TODO: Rewrite this with HQL
 */
public abstract class BaseObsCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    protected static final Log log = LogFactory.getLog(BaseObsCohortDefinitionEvaluator.class);

    /**
     * Encapsulates the common logic between getPatientsHavingRangedObs and getPatientsHavingDiscreteObs
     *
     * The arguments passed in fall into two types:
     * <ol>
     *     <li>arguments that limit which obs we will look at</li>
     *     <ul>
     *         <li>cd</li>
     *     </ul>
     *     <li>arguments that the obs values must match after being limited by the above arguments</li>
     *     <ul>
     *         <li>operator1</li>
     *         <li>value1</li>
     *         <li>operator2</li>
     *         <li>value2</li>
     *         <li>setOperator</li>
     *         <li>valueList</li>
     *     </ul>
     * </ol>
     */
    protected Cohort getPatientsHavingObs(BaseObsCohortDefinition cd,
                                          RangeComparator operator1, Object value1,
                                          RangeComparator operator2, Object value2,
                                          SetComparator setOperator, List<? extends Object> valueList,
                                          EvaluationContext context) {

        if (cd.getGroupingConcept() != null) {
            throw new RuntimeException("grouping concept not yet implemented");
        }

        boolean joinOnEncounter = cd.getEncounterTypeIds() != null;
        String dateAndLocationSql = ""; // TODO rename to include encounterType
        String dateAndLocationSqlForSubquery = "";
        if (cd.getOnOrAfter() != null) {
            dateAndLocationSql += " and o.obs_datetime >= :onOrAfter ";
            dateAndLocationSqlForSubquery += " and obs.obs_datetime >= :onOrAfter ";
        }
        if (cd.getOnOrBefore() != null) {
            dateAndLocationSql += " and o.obs_datetime <= :onOrBefore ";
            dateAndLocationSqlForSubquery += " and obs.obs_datetime <= :onOrBefore ";
        }
        if (cd.getLocationIds() != null) {
            dateAndLocationSql += " and o.location_id in (:locationIds) ";
            dateAndLocationSqlForSubquery += " and obs.location_id in (:locationIds) ";
        }
        if (cd.getEncounterTypeIds() != null) {
            dateAndLocationSql += " and e.encounter_type in (:encounterTypeIds) ";
            dateAndLocationSqlForSubquery += " and encounter.encounter_type in (:encounterTypeIds) ";
        }

        TimeModifier tm = cd.getTimeModifier();
        if (tm == null) {
            tm = TimeModifier.ANY;
        }
        boolean doSqlAggregation = tm == TimeModifier.MIN || tm == TimeModifier.MAX || tm == TimeModifier.AVG;
        boolean doInvert = tm == TimeModifier.NO;

        String valueSql = null;
        List<String> valueClauses = new ArrayList<String>();
        List<Object> valueListForQuery = null;

        if (value1 != null || value2 != null) {
            valueSql = (value1 != null && value1 instanceof Number) ? " o.value_numeric " : " o.value_datetime ";
        }
        else if (valueList != null && valueList.size() > 0) {
            valueListForQuery = new ArrayList<Object>();
            if (valueList.get(0) instanceof String) {
                valueSql = " o.value_text ";
                for (Object o : valueList) {
                    valueListForQuery.add(o);
                }
            }
            else {
                valueSql = " o.value_coded ";
                for (Object o : valueList) {
                    if (o instanceof Concept) {
                        valueListForQuery.add(((Concept) o).getConceptId());
                    }
                    else if (o instanceof Number) {
                        valueListForQuery.add(((Number) o).intValue());
                    }
                    else {
                        throw new IllegalArgumentException("Don't know how to handle " + o.getClass() + " in valueList");
                    }
                }
            }
        }

        if (doSqlAggregation) {
            valueSql = " " + tm.toString() + "(" + valueSql + ") ";
        }

        if (value1 != null || value2 != null) {
            if (value1 != null) {
                valueClauses.add(valueSql + operator1.getSqlRepresentation() + " :value1 ");
            }
            if (value2 != null) {
                valueClauses.add(valueSql + operator2.getSqlRepresentation() + " :value2 ");
            }
        }
        else if (valueList != null && valueList.size() > 0) {
            valueClauses.add(valueSql + setOperator.getSqlRepresentation() + " (:valueList) ");
        }

        StringBuilder sql = new StringBuilder();
        sql.append(" select o.person_id from obs o ");
        sql.append(" inner join patient p on o.person_id = p.patient_id ");
        if (joinOnEncounter) {
            sql.append(" inner join encounter e on o.encounter_id = e.encounter_id ");
        }

        if (tm == TimeModifier.ANY || tm == TimeModifier.NO) {
            sql.append(" where o.voided = false and p.voided = false ");
            if (cd.getQuestion() != null) {
                sql.append(" and concept_id = :questionConceptId ");
            }
            sql.append(dateAndLocationSql);
        }
        else if (tm == TimeModifier.FIRST || tm == TimeModifier.LAST) {
            boolean isFirst = tm == TimeModifier.FIRST;
            sql.append(" inner join ( ");
            sql.append("    select person_id, " + (isFirst ? "MIN" : "MAX") + "(obs_datetime) as odt ");
            sql.append("    from obs ");
            if (joinOnEncounter) {
                sql.append(" inner join encounter on obs.encounter_id = encounter.encounter_id ");
            }
            sql.append("             where obs.voided = false and obs.concept_id = :questionConceptId " + dateAndLocationSqlForSubquery + " group by person_id ");
            sql.append(" ) subq on o.person_id = subq.person_id and o.obs_datetime = subq.odt ");
            sql.append(" where o.voided = false and p.voided = false and o.concept_id = :questionConceptId ");
            sql.append(dateAndLocationSql);
        }
        else if (doSqlAggregation) {
            sql.append(" where o.voided = false and p.voided = false and concept_id = :questionConceptId " + dateAndLocationSql );
            sql.append(" group by o.person_id ");
        }
        else {
            throw new IllegalArgumentException("TimeModifier '" + tm + "' not recognized");
        }

        if (valueClauses.size() > 0) {
            sql.append(doSqlAggregation ? " having " : " and ");
            for (Iterator<String> i = valueClauses.iterator(); i.hasNext(); ) {
                sql.append(i.next());
                if (i.hasNext()) {
                    sql.append(" and ");
                }
            }
        }

        log.debug("sql: " + sql);

        SqlQueryBuilder qb = new SqlQueryBuilder();
        qb.append(sql.toString());

        if (cd.getQuestion() != null) {
            qb.addParameter("questionConceptId", cd.getQuestion());
        }
        if (value1 != null) {
            qb.addParameter("value1", value1);
        }
        if (value2 != null) {
            qb.addParameter("value2", value2);
        }
        if (valueListForQuery != null) {
            qb.addParameter("valueList", valueListForQuery);
        }
        if (cd.getOnOrAfter() != null) {
            qb.addParameter("onOrAfter", cd.getOnOrAfter());
        }
        if (cd.getOnOrBefore() != null) {
            qb.addParameter("onOrBefore", DateUtil.getEndOfDayIfTimeExcluded(cd.getOnOrBefore()));
        }
        if (cd.getLocationIds() != null) {
            qb.addParameter("locationIds", cd.getLocationIds());
        }
        if (cd.getEncounterTypeIds() != null) {
            qb.addParameter("encounterTypeIds", cd.getEncounterTypeIds());
        }

        List<Integer> ids = Context.getService(EvaluationService.class).evaluateToList(qb, Integer.class, context);

        if (doInvert) {
            Set<Integer> inverted = Cohorts.allPatients(context).getMemberIds();
            inverted.removeAll(ids);
            return new Cohort(inverted);
        }
        else {
            return new Cohort(ids);
        }
    }

}
