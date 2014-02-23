package org.openmrs.module.reporting.query.encounter.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.MappedParametersEncounterQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;

@Handler(supports = MappedParametersEncounterQuery.class)
public class MappedParametersEncounterQueryEvaluator implements EncounterQueryEvaluator {

    @Override
    public EncounterQueryResult evaluate(EncounterQuery cohortDefinition, EvaluationContext context) throws EvaluationException {
		MappedParametersEncounterQuery q = (MappedParametersEncounterQuery) cohortDefinition;
		EncounterQueryService service = Context.getService(EncounterQueryService.class);
		EncounterQueryResult evaluated = service.evaluate(q.getWrapped(), context);
        evaluated.setDefinition(q);
        return evaluated;
    }

}
