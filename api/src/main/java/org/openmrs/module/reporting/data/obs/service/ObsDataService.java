package org.openmrs.module.reporting.data.obs.service;

import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * API for evaluating an ObsDataDefinition across a set of Obs
 */
public interface ObsDataService extends DefinitionService<ObsDataDefinition> {

	/**
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
    public EvaluatedObsData evaluate(ObsDataDefinition definition, EvaluationContext context) throws EvaluationException;

	/**
	 * @see DefinitionService#evaluate(Mapped, EvaluationContext)
	 */
    public EvaluatedObsData evaluate(Mapped<? extends ObsDataDefinition> mappedDefinition, EvaluationContext context) throws EvaluationException;

}
