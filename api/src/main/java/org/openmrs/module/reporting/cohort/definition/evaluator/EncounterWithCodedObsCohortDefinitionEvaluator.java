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

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterWithCodedObsCohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

@Handler(supports = EncounterWithCodedObsCohortDefinition.class)
public class EncounterWithCodedObsCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

        EncounterWithCodedObsCohortDefinition cd = (EncounterWithCodedObsCohortDefinition) cohortDefinition;
		context = ObjectUtil.nvl(context, new EvaluationContext());
		EvaluatedCohort ret = new EvaluatedCohort(null, cd, context);

		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append("select distinct e.patient_id");
		q.append("from encounter e");
		q.append((cd.isIncludeNoObsValue() ? "left outer" : "inner") + " join obs o");
        q.append("on e.encounter_id = o.encounter_id and o.voided = false");
        if (cd.getConcept() != null) {
			q.append("and o.concept_id = :concept").addParameter("concept", cd.getConcept());
        }
        q.append("where e.voided = false");
        if (cd.getEncounterTypeList() != null) {
           q.append("and e.encounter_type in (:etList)").addParameter("etList", cd.getEncounterTypeList());
        }
        if (cd.getLocationList() != null) {
			q.append("and e.location_id in (:locationList)").addParameter("locationList", cd.getLocationList());
        }

		if (cd.isIncludeNoObsValue() && cd.getIncludeCodedValues() == null && cd.getExcludeCodedValues() == null) {
			q.append("and o.value_coded is null");
		}
		if (cd.getIncludeCodedValues() != null) {
			if (cd.isIncludeNoObsValue()) {
				q.append("and (o.value_coded is null or o.value_coded in (:includeCodedValues))");
			}
			else {
				q.append("and o.value_coded in (:includeCodedValues)");
			}
			q.addParameter("includeCodedValues", cd.getIncludeCodedValues());
		}
		if (cd.getExcludeCodedValues() != null) {
			if (cd.isIncludeNoObsValue()) {
				q.append("and (o.value_coded is null or o.value_coded not in (:excludeCodedValues))");
			}
			else {
				q.append("and o.value_coded not in (:excludeCodedValues)");
			}
			q.addParameter("excludeCodedValues", cd.getExcludeCodedValues());
		}

        if (cd.getOnOrAfter() != null) {
			q.append("and e.encounter_datetime >= :onOrAfter").addParameter("onOrAfter", cd.getOnOrAfter());
        }
        if (cd.getOnOrBefore() != null) {
			q.append("and e.encounter_datetime <= :onOrBefore").addParameter("onOrBefore", DateUtil.getEndOfDayIfTimeExcluded(cd.getOnOrBefore()));
        }

		List<Integer> results = evaluationService.evaluateToList(q, Integer.class, context);
		ret.setMemberIds(new HashSet<Integer>(results));

		return ret;
    }
}
