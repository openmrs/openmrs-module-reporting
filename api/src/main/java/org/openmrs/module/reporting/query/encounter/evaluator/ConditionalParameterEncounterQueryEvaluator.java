package org.openmrs.module.reporting.query.encounter.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.ConditionalParameterEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = ConditionalParameterEncounterQuery.class)
public class ConditionalParameterEncounterQueryEvaluator implements EncounterQueryEvaluator {

    @Autowired
	EncounterQueryService encounterQueryService;

    @Override
    public EncounterQueryResult evaluate(EncounterQuery encounterQuery, EvaluationContext context) throws EvaluationException {
		ConditionalParameterEncounterQuery q = (ConditionalParameterEncounterQuery) encounterQuery;
		EncounterQueryResult ret = new EncounterQueryResult(encounterQuery, context);

		Object valueToCheck = context.getParameterValue(q.getParameterToCheck());
		Mapped<? extends EncounterQuery> match = q.getConditionalQueries().get(valueToCheck);
		if (match == null) {
			match = q.getDefaultQuery();
		}
		if (match != null) {
			EncounterQueryResult r  = encounterQueryService.evaluate(match, context);
			ret.getMemberIds().addAll(r.getMemberIds());
		}
		return ret;
    }
}
