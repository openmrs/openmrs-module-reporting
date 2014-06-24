package org.openmrs.module.reporting.data.obs.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.definition.GroupMemberObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Handler(supports=GroupMemberObsDataDefinition.class, order=50)
public class GroupMemberObsDataEvaluator implements ObsDataEvaluator {

    protected final Log log = LogFactory.getLog(getClass());

    @Autowired
	EvaluationService evaluationService;

    @Override
    public EvaluatedObsData evaluate(ObsDataDefinition definition, EvaluationContext context) throws EvaluationException {
        GroupMemberObsDataDefinition def = (GroupMemberObsDataDefinition) definition;
        EvaluatedObsData data = new EvaluatedObsData(definition, context);

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("o.obsGroup.id", "o");
		q.from(Obs.class, "o");
		q.whereEqual("o.concept", def.getQuestion());
		q.whereObsIn("o.obsGroup.id", context);

		List<Object[]> result = Context.getService(EvaluationService.class).evaluateToList(q, context);
		for (Object[] row : result) {
			Integer obsGroupId = (Integer)row[0];
			Obs obs = (Obs)row[1];

			if (!def.isSingleObs()) {
				List l = (List) data.getData().get(obsGroupId);
				if (l == null) {
					l = new ArrayList();
					data.getData().put(obsGroupId, l);
				}
				l.add(obs);
			}
			else {
				// If there are multiple matching obs and we are in singleObs mode then last one wins
				if (data.getData().get(obsGroupId) != null) {
					log.warn("Multiple matching obs for obsgroup " + obsGroupId + "... picking one");
				}
				data.addData(obsGroupId, obs);
			}
		}

        return data;
    }

}
