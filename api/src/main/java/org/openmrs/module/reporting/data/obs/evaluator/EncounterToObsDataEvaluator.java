package org.openmrs.module.reporting.data.obs.evaluator;

import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.definition.EncounterToObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Map;

/**
 * Evaluates a EncounterToObsDataDefinition to produce a ObsData
 */
@Handler(supports=EncounterToObsDataDefinition.class, order=50)
public class EncounterToObsDataEvaluator implements ObsDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

    /**
     *  @should return encounter data for each obs in the passed context
     */
    @Override
    public EvaluatedObsData evaluate(ObsDataDefinition definition, EvaluationContext context) throws EvaluationException {

        EvaluatedObsData c = new EvaluatedObsData(definition, context);

		// create a map of obs ids -> encounter ids

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("o.obsId", "o.encounter.encounterId");
		q.from(Obs.class, "o");
		q.whereObsIn("o.obsId", context);

		Map<Integer, Integer> convertedIds = evaluationService.evaluateToMap(q, Integer.class, Integer.class, context);

		if (!convertedIds.keySet().isEmpty()) {

			// Create a new (encounter) evaluation context using the retrieved ids
			EncounterEvaluationContext encounterEvaluationContext = new EncounterEvaluationContext();
			EncounterIdSet baseEncounters = new EncounterIdSet();
			for (Integer encounterId : convertedIds.values()) {
				if (encounterId != null) {
					baseEncounters.add(encounterId);
				}
			}
			encounterEvaluationContext.setBaseEncounters(baseEncounters);

			// evaluate the joined definition via this encounter context
			EncounterToObsDataDefinition def = (EncounterToObsDataDefinition) definition;
			EvaluatedEncounterData ed = Context.getService(EncounterDataService.class).evaluate(def.getJoinedDefinition(), encounterEvaluationContext);

			// now create the result set by mapping the results in the encounter data set to obs ids
			for (Integer obsId : convertedIds.keySet()) {
				c.addData(obsId, ed.getData().get(convertedIds.get(obsId)));
			}
		}

        return c;
    }
}
