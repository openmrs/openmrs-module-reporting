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
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Evaluates a SQL query and returns a Cohort
 */
@Handler(supports={SqlCohortDefinition.class})
public class SqlCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
	EvaluationService evaluationService;
	
	/**
     * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
     * 
     * @should support integer parameter
     * @should support string parameter
     * @should support patient parameter
     * @should support integer list parameter
     * @should support patient list parameter
     * @should support cohort parameter
     * @should support date parameter
     * @should should protect SQL Query Against database modifications
     */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		context = ObjectUtil.nvl(context, new EvaluationContext());
    	SqlCohortDefinition sqlCohortDefinition = (SqlCohortDefinition) cohortDefinition;
		EvaluatedCohort ret = new EvaluatedCohort(sqlCohortDefinition, context);

		// Return an empty result if the base cohort is not-null and empty
		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return ret;
		}

		final Boolean includeChildLocations = (Boolean) context.getParameterValue("includeChildLocations");
		if (includeChildLocations != null && includeChildLocations) {
			final List<Location> locationList = (List<Location>) context.getParameterValue("locationList");
			final Map<String, Object> parameterValues = context.getParameterValues();
			parameterValues.put("locationList", getLocationsIncludingChildLocations(locationList));
			context.setParameterValues(parameterValues);
		}

		SqlQueryBuilder qb = new SqlQueryBuilder(sqlCohortDefinition.getQuery(), context.getParameterValues());
		if (sqlCohortDefinition.getQuery().contains(":patientIds")) {
			qb.addParameter("patientIds", context.getBaseCohort());
		}

		List<Integer> l = evaluationService.evaluateToList(qb, Integer.class, context);
    	if (context.getBaseCohort() != null) {
    		l.retainAll(context.getBaseCohort().getMemberIds());
    	}
		ret.setMemberIds(new HashSet<Integer>(l));

		return ret;
    }

    private List<Location> getLocationsIncludingChildLocations(List<Location> locationList) {
		 return DefinitionUtil.getAllLocationsAndChildLocations(locationList);
	}
}