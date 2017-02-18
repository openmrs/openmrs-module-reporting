/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.Location;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterWithCodedObsCohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.definition.DefinitionUtil;
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
			q.append("and e.location_id in (:locationList)").addParameter("locationList", getLocationList(cd));
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

    private List<Location> getLocationList(EncounterWithCodedObsCohortDefinition cd) {
    	if (cd.isIncludeChildLocations()) {
    		return DefinitionUtil.getAllLocationsAndChildLocations(cd.getLocationList());
		}
		return cd.getLocationList();
	}
}
