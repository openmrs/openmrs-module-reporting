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
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

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
}