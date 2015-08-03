package org.openmrs.module.reporting.query.obs.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.query.obs.ObsQueryResult;
import org.openmrs.module.reporting.query.obs.definition.MappedParametersObsQuery;
import org.openmrs.module.reporting.query.obs.definition.ObsQuery;
import org.openmrs.module.reporting.query.obs.service.ObsQueryService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = MappedParametersObsQuery.class)
public class MappedParametersObsQueryEvaluator implements ObsQueryEvaluator {

    @Autowired
    ObsQueryService obsQueryService;

    @Override
    public ObsQueryResult evaluate(ObsQuery obsQuery, EvaluationContext context) throws EvaluationException {
        MappedParametersObsQuery q = (MappedParametersObsQuery) obsQuery;
        ObsQueryResult evaluated = obsQueryService.evaluate(q.getWrapped(), context);
        evaluated.setDefinition(q);
        return evaluated;
    }

}
