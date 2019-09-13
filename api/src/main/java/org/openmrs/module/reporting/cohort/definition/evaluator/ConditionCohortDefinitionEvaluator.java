package org.openmrs.module.reporting.cohort.definition.evaluator;

import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ConditionCohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { ConditionCohortDefinition.class })
public class ConditionCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	@Autowired
	EvaluationService evaluationService;
	
	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		
		ConditionCohortDefinition cd = (ConditionCohortDefinition) cohortDefinition;
		StringBuilder sql = new StringBuilder();
		SqlQueryBuilder query = new SqlQueryBuilder();
		
		String sqlDefault = "SELECT patient_id FROM conditions c";
		String sqlDate = "";
		String sqlConditonValue = "";
		sql.append(sqlDefault);
		if (cd.getOnOrAfter() != null ) {
			sqlDate = " AND c.date_created >= :onOrAfter";
		}
		if (cd.getOnOrBefore() != null ) {
			sqlDate += " AND c.date_created <= :onOrBefore ";
		}
		if (cd.getConcept() != null) {
			sqlConditonValue = " WHERE c.concept_id = :conceptId";
		}
		if (cd.getConditionNonCoded() != null) {
			sqlConditonValue += " AND c.condition_non_coded = :conditonNonCoded ";
		}
		if (cd.getConcept() != null) {
			sql.append(sqlConditonValue);
			sql.append(sqlDate);
		}
		query.append(sql.toString());
		if (cd.getOnOrAfter() != null) {
			query.addParameter("onOrAfter", cd.getOnOrAfter());
		}
		if (cd.getOnOrBefore() != null) {
			query.addParameter("onOrBefore", DateUtil.getEndOfDayIfTimeExcluded(cd.getOnOrBefore()));
		}
		if (cd.getConcept() != null) {
			query.addParameter("conceptId", cd.getConcept().getId());
		}
		if (cd.getConditionNonCoded() != null) {
			query.addParameter("conditonNonCoded", cd.getConditionNonCoded());
		}
		List<Integer> patientIds = evaluationService.evaluateToList(query, Integer.class, context);
		Cohort cohort = new Cohort(patientIds);
		return new EvaluatedCohort(cohort, cd, context);
	}
}
