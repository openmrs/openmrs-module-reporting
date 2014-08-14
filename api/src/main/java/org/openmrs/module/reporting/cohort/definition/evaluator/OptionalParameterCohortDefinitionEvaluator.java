package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.OptionalParameterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = OptionalParameterCohortDefinition.class)
public class OptionalParameterCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    CohortDefinitionService cohortDefinitionService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		OptionalParameterCohortDefinition cd = (OptionalParameterCohortDefinition) cohortDefinition;
		boolean hasParameters = true;
		for (String paramName : cd.getParametersToCheck()) {
			if (context.getParameterValue(paramName) == null) {
				hasParameters = false;
			}
		}
		Cohort ret;
		if (hasParameters) {
			ret = cohortDefinitionService.evaluate(cd.getWrappedCohortDefinition(), context);
		}
		else {
			ret = cohortDefinitionService.evaluate(new AllPatientsCohortDefinition(), context);
		}
		return new EvaluatedCohort(ret, cd, context);
    }
}
