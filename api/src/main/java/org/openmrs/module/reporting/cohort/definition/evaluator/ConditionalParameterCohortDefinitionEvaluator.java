package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ConditionalParameterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = ConditionalParameterCohortDefinition.class)
public class ConditionalParameterCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    CohortDefinitionService cohortDefinitionService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		ConditionalParameterCohortDefinition cd = (ConditionalParameterCohortDefinition) cohortDefinition;
		EvaluatedCohort ret = new EvaluatedCohort(cohortDefinition, context);

		Object valueToCheck = context.getParameterValue(cd.getParameterToCheck());
		Mapped<? extends CohortDefinition> match = cd.getConditionalCohortDefinitions().get(valueToCheck);
		if (match == null) {
			match = cd.getDefaultCohortDefinition();
		}
		if (match != null) {
			Cohort c  = cohortDefinitionService.evaluate(match, context);
			ret.getMemberIds().addAll(c.getMemberIds());
		}
		return ret;
    }
}
