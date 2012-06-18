package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.NumericObsCohortDefinition;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates a NumericObsCohortDefinition and produces a Cohort
 */
@Handler(supports={NumericObsCohortDefinition.class})
public class NumericObsCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	public NumericObsCohortDefinitionEvaluator() { } 
	
	/**
	 * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 * 
	 * @should get patients with any obs of a specified concept
	 * @should test any with many properties specified
	 * @should test avg with many properties specified
	 * @should test last with many properties specified 
	 * @should should find patients with obs within the specified time frame
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		NumericObsCohortDefinition cd = (NumericObsCohortDefinition) cohortDefinition;
		
		Cohort c = Context.getService(CohortQueryService.class).getPatientsHavingRangedObs(
			cd.getTimeModifier(), cd.getQuestion(), cd.getGroupingConcept(),
			cd.getOnOrAfter(), cd.getOnOrBefore(),
			cd.getLocationList(), cd.getEncounterTypeList(),
			cd.getOperator1(), cd.getValue1(),
			cd.getOperator2(), cd.getValue2());
		
		return new EvaluatedCohort(c, cohortDefinition, context);
	}
	
}
