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

import java.io.StringReader;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.util.SqlScriptParser;

/**
 * Evaluates a SQL query and returns a Cohort
 */
@Handler(supports={SqlCohortDefinition.class})
public class SqlCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * Default Constructor
	 */
	public SqlCohortDefinitionEvaluator() {}
	
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
    	SqlCohortDefinition sqlCohortDefinition = (SqlCohortDefinition) cohortDefinition;
    	CohortQueryService cqs = Context.getService(CohortQueryService.class);
    	String sql = SqlScriptParser.parse(new StringReader(sqlCohortDefinition.getQuery()))[0];
    	Cohort c = cqs.executeSqlQuery(sql, context.getParameterValues());
    	if (context.getBaseCohort() != null) {
    		c = Cohort.intersect(c, context.getBaseCohort());
    	}
    	return new EvaluatedCohort(c, cohortDefinition, context);
    }
}