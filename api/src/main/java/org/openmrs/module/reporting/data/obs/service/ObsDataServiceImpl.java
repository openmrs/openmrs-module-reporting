package org.openmrs.module.reporting.data.obs.service;

import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 *
 */
public class ObsDataServiceImpl extends BaseDefinitionService<ObsDataDefinition> implements ObsDataService {

    @Override
    public Class<ObsDataDefinition> getDefinitionType() {
        return ObsDataDefinition.class;
    }

    @Override
    public EvaluatedObsData evaluate(ObsDataDefinition definition, EvaluationContext context) throws EvaluationException {
        return (EvaluatedObsData)super.evaluate(definition, context);
    }

    @Override
    public EvaluatedObsData evaluate(Mapped<? extends ObsDataDefinition> mappedDefinition, EvaluationContext context) throws EvaluationException {
        return (EvaluatedObsData)super.evaluate(mappedDefinition, context);
    }

}
