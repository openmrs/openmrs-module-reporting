package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateObsCohortDefinition;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates a DateObsCohortDefinition and produces a Cohort
 */
@Handler(supports={DateObsCohortDefinition.class})
public class DateObsCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	public DateObsCohortDefinitionEvaluator() { }
	
	/**
	 * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 * 
	 * @should test any with many properties specified
	 * @should find nobody if no patients match
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		DateObsCohortDefinition cd = (DateObsCohortDefinition) cohortDefinition;
		
		Cohort c = Context.getService(CohortQueryService.class).getPatientsHavingRangedObs(
			cd.getTimeModifier(), cd.getQuestion(), cd.getGroupingConcept(),
			cd.getOnOrAfter(), cd.getOnOrBefore(),
			cd.getLocationList(), cd.getEncounterTypeList(),
			cd.getOperator1(), cd.getValue1(),
			cd.getOperator2(), cd.getValue2());
		
		return new EvaluatedCohort(c, cohortDefinition, context);
	}
	
}
