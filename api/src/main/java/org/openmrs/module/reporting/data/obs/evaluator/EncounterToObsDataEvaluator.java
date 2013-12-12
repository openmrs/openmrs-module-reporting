package org.openmrs.module.reporting.data.obs.evaluator;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.ObsDataUtil;
import org.openmrs.module.reporting.data.obs.definition.EncounterToObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Evaluates a EncounterToObsDataDefinition to produce a ObsData
 */
@Handler(supports=EncounterToObsDataDefinition.class, order=50)
public class EncounterToObsDataEvaluator implements ObsDataEvaluator {

    /**
     *  @should return encounter data for each obs in the passed context
     */
    @Override
    public EvaluatedObsData evaluate(ObsDataDefinition definition, EvaluationContext obsEvaluationContext) throws EvaluationException {

        DataSetQueryService dqs = Context.getService(DataSetQueryService.class);
        EvaluatedObsData c = new EvaluatedObsData(definition, obsEvaluationContext);

        Set<Integer> obsIds = ObsDataUtil.getObsIdsForContext(obsEvaluationContext, false);

        // just return empty set if input set is empty
        if (obsIds.size() == 0) {
            return c;
        }

        // create a map of obs ids -> encounter ids (note assumption that personId = patientId)
        Map<Integer, Integer> convertedIds = dqs.convertData(Encounter.class, "encounterId", null, Obs.class, "encounter.encounterId", obsIds);

        // create a new (encounter) evaluation context using the retrieved ids
        EncounterEvaluationContext encounterEvaluationContext = new EncounterEvaluationContext();
        encounterEvaluationContext.setBaseEncounters(new EncounterIdSet(new HashSet<Integer>(convertedIds.values())));

        // evaluate the joined definition via this encounter context
        EncounterToObsDataDefinition def = (EncounterToObsDataDefinition) definition;
        EvaluatedEncounterData ed = Context.getService(EncounterDataService.class).evaluate(def.getJoinedDefinition(), encounterEvaluationContext);

        // now create the result set by mapping the results in the encounter data set to obs ids
        for (Integer obsId : obsIds) {
            c.addData(obsId, ed.getData().get(convertedIds.get(obsId)));
        }
        return c;

    }
}
