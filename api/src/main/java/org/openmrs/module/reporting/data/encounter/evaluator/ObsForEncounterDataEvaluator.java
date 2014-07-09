package org.openmrs.module.reporting.data.encounter.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.ObsForEncounterDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates a ObsForEncounterDataDefinition to produce EncounterData
 */
@Handler(supports=ObsForEncounterDataDefinition.class, order=50)
public class ObsForEncounterDataEvaluator implements EncounterDataEvaluator {

    protected final Log log = LogFactory.getLog(getClass());
    
    @Override
    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        
        ObsForEncounterDataDefinition def = (ObsForEncounterDataDefinition) definition;
        EvaluatedEncounterData data = new EvaluatedEncounterData();

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("obs.encounter.encounterId, obs");
		q.from(Obs.class, "obs");
		q.whereEqual("obs.concept", def.getQuestion());
		q.whereEncounterIn("obs.encounter.encounterId", context);

		List<Object[]> result = Context.getService(EvaluationService.class).evaluateToList(q, context);
        for (Object[] row : result) {
            Integer encounterId = (Integer)row[0];
			Obs obs = (Obs)row[1];

			if (!def.isSingleObs()) {
				List l = (List) data.getData().get(encounterId);
				if (l == null) {
					l = new ArrayList();
					data.getData().put(encounterId, l);
				}
				l.add(obs);
			}
			else {
				// If there are multiple matching obs and we are in singleObs mode then last one wins
				if (data.getData().get(encounterId) != null) {
					log.warn("Multiple matching obs for encounter " + encounterId + "... picking one");
				}
				data.addData(encounterId, obs);
			}
        }

        return data;
    }
}
