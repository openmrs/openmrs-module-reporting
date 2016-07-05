package org.openmrs.module.reporting.dataset.definition;

import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Normally each reporting definition is a simple DTO, and its logic lives in an associated evaluator. This class allows a
 * simpler approach, by letting concrete subclasses directly define an evaluate() method, instead of having this be done
 * in a separate class.
 * This combines well with
 * {@link org.openmrs.module.reporting.definition.library.implementerconfigured.BaseImplementerConfiguredDefinitionLibrary}
 */
public abstract class EvaluatableDataSetDefinition extends BaseDataSetDefinition {
	
	public abstract DataSet evaluate(EvaluationContext evalContext);

}
