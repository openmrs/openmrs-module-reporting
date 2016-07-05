package org.openmrs.module.reporting.cohort.definition;

import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Normally each reporting definition is a simple DTO, and its logic lives in an associated evaluator. This class allows a
 * simpler approach, by letting concrete subclasses directly define an evaluate() method, instead of having this be done
 * in a separate class.
 * This combines well with
 * {@link org.openmrs.module.reporting.definition.library.implementerconfigured.BaseImplementerConfiguredDefinitionLibrary}
 */
public abstract class EvaluatableCohortDefinition extends BaseCohortDefinition {

	public abstract EvaluatedCohort evaluate(EvaluationContext context);

}