package org.openmrs.module.reporting.data.encounter.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.QueryBuilder;
import org.openmrs.module.reporting.data.encounter.EncounterDataUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.ObsForEncounterDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

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

		QueryBuilder qb = new QueryBuilder();
		qb.addClause("select 	obs.encounter.encounterId, obs");
		qb.addClause("from		Obs as obs");
		qb.addClause("where		voided = false");
		qb.addClause("and		concept = :question").withParameter("question", def.getQuestion());
		if (encIds != null) {
			qb.addClause("and	encounter.encounterId in (:encIds)").withParameter("encIds", encIds);
		}
		List<Object[]> results = (List<Object[]>)qb.execute();

        // Create an entry for each encounter
        for (Integer encId : encIds) {
            if (!def.isSingleObs()) {
                data.addData(encId, new ArrayList<Obs>());
            }
            else {
                data.addData(encId, null);
            }
        }

        // Now populate with actual results
        for (Object[] result : results) {
            Integer encId = (Integer)result[0];
			Obs obs = (Obs)result[1];

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
