package org.openmrs.module.reporting.data.encounter.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.encounter.EncounterDataUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.ObsForEncounterDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

        Set<Integer> encIds = EncounterDataUtil.getEncounterIdsForContext(context, false);

        // just return empty set if input set is empty
        if (encIds.size() == 0) {
            return data;
        }

		// Create an entry for each encounter
		for (Integer encId : encIds) {
			if (!def.isSingleObs()) {
				data.addData(encId, new ArrayList<Obs>());
			}
			else {
				data.addData(encId, null);
			}
		}

		HqlQueryBuilder qb = new HqlQueryBuilder();
		qb.select("obs.encounter.encounterId, obs");
		qb.from(Obs.class, "obs");
		qb.whereEqual("obs.voided", false);
		qb.whereEqual("obs.concept", def.getQuestion());
		qb.whereEqual("obs.encounter.encounterId", encIds); // TODO: Should we just join on the 2 individual sets in the context?

		List<Object[]> result = Context.getService(EvaluationService.class).evaluateToList(qb);
        for (Object[] row : result) {
            Integer encId = (Integer)row[0];
			Obs obs = (Obs)row[1];

            if (!def.isSingleObs()) {
                ((List<Obs>) data.getData().get(encId)).add(obs);
            }
            else {
				if (data.getData().get(encId) != null) {
					log.warn("Multiple matching obs for encounter <" + encId + ">. The last one will be returned.");
				}
                data.addData(encId, obs);
            }
        }

        return data;
    }
}
