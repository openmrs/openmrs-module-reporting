package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates a CodedObsCohortDefinition and produces a Cohort
 */
@Handler(supports={CodedObsCohortDefinition.class})
public class CodedObsCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	/**
	 * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
	 * 
	 * @should test any with many properties specified
	 * @should test last with many properties specified
	 * @should not return voided patients
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		CodedObsCohortDefinition cd = (CodedObsCohortDefinition) cohortDefinition;
		
		Cohort c = Context.getService(CohortQueryService.class).getPatientsHavingDiscreteObs(
			cd.getTimeModifier(), cd.getQuestion(), cd.getGroupingConcept(),
			cd.getOnOrAfter(), cd.getOnOrBefore(),
			cd.getLocationList(), cd.getEncounterTypeList(),
			cd.getOperator(), cd.getValueList());
		return new EvaluatedCohort(c, cohortDefinition, context);
	}
	
}
