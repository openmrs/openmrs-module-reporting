package org.openmrs.module.reporting.data.obs.service;

import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
@Transactional
public interface ObsDataService extends DefinitionService<ObsDataDefinition> {

    @Transactional(readOnly = true)
    public EvaluatedObsData evaluate(ObsDataDefinition definition, EvaluationContext context) throws EvaluationException;

    @Transactional(readOnly = true)
    public EvaluatedObsData evaluate(Mapped<? extends ObsDataDefinition> mappedDefinition, EvaluationContext context) throws EvaluationException;

}
