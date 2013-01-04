package org.openmrs.module.reporting.cohort.definition.evaluator;

import java.util.Date;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.BirthAndDeathCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports={BirthAndDeathCohortDefinition.class})
public class BirthAndDeathCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
    public BirthAndDeathCohortDefinitionEvaluator() { }
	
	/**
	 * @see org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator#evaluate(org.openmrs.module.reporting.cohort.definition.CohortDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * 
	 * @should find patients by birth range
	 * @should find patients by death range
	 * @should find patients by birth range and death range
	 * @should find patients born on the onOrBefore date if passed in time is at midnight
	 * @should find patients that died on the onOrBefore date if passed in time is at midnight
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		BirthAndDeathCohortDefinition definition = (BirthAndDeathCohortDefinition) cohortDefinition;
		Date bornOnOrBeforeDate = definition.getBornOnOrBefore();
		if (bornOnOrBeforeDate != null)
			bornOnOrBeforeDate = DateUtil.getEndOfDayIfTimeExcluded(definition.getBornOnOrBefore());
		Date diedOnOrBeforeDate = definition.getDiedOnOrBefore();
		if (diedOnOrBeforeDate != null)
			diedOnOrBeforeDate = DateUtil.getEndOfDayIfTimeExcluded(definition.getDiedOnOrBefore());
		
		Cohort c = Context.getService(CohortQueryService.class).getPatientsHavingBirthAndDeath(
		    definition.getBornOnOrAfter(), bornOnOrBeforeDate, definition.getDiedOnOrAfter(), diedOnOrBeforeDate);
		return new EvaluatedCohort(c, cohortDefinition, context);
	}
	
}
