package org.openmrs.module.reporting.data.obs.service;

import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * Base Implementation of ObsDataService
 */
public class ObsDataServiceImpl extends BaseDefinitionService<ObsDataDefinition> implements ObsDataService {

	/**
	 * @see DefinitionService#getDefinitionType()
	 */
    @Override
    public Class<ObsDataDefinition> getDefinitionType() {
        return ObsDataDefinition.class;
    }

	/**
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
    @Override
    public EvaluatedObsData evaluate(ObsDataDefinition definition, EvaluationContext context) throws EvaluationException {
        return (EvaluatedObsData)super.evaluate(definition, context);
    }

	/**
	 * @see DefinitionService#evaluate(Mapped, EvaluationContext)
	 */
    @Override
    public EvaluatedObsData evaluate(Mapped<? extends ObsDataDefinition> mappedDefinition, EvaluationContext context) throws EvaluationException {
        return (EvaluatedObsData)super.evaluate(mappedDefinition, context);
    }

}
