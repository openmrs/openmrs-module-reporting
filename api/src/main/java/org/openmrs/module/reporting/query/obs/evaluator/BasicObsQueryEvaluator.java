package org.openmrs.module.reporting.query.obs.evaluator;

import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.obs.ObsIdSet;
import org.openmrs.module.reporting.query.obs.ObsQueryResult;
import org.openmrs.module.reporting.query.obs.definition.BasicObsQuery;
import org.openmrs.module.reporting.query.obs.definition.ObsQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Evaluates a BasicObsQuery
 */
@Handler(supports = BasicObsQuery.class)
public class BasicObsQueryEvaluator implements ObsQueryEvaluator {

    @Autowired
	EvaluationService evaluationService;

    @Override
    public ObsQueryResult evaluate(ObsQuery definition, EvaluationContext context) {
        context = ObjectUtil.nvl(context, new EvaluationContext());
        BasicObsQuery query = (BasicObsQuery) definition;
        ObsQueryResult result = new ObsQueryResult(query, context);

		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return result;
		}
		if (context instanceof ObsEvaluationContext) {
			ObsIdSet basObs = ((ObsEvaluationContext) context).getBaseObs();
			if (basObs != null && basObs.getMemberIds().isEmpty()) {
				return result;
			}
		}

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("o.obsId");
		q.from(Obs.class, "o");
		q.whereIn("o.concept", query.getConceptList());
		q.whereGreaterOrEqualTo("o.obsDatetime", query.getOnOrAfter());
		q.whereLessOrEqualTo("o.obsDatetime", query.getOnOrBefore());
		q.whereObsIn("o.obsId", context);

		List<Integer> results = evaluationService.evaluateToList(q, Integer.class, context);
		result.getMemberIds().addAll(results);

		return result;
    }

}
