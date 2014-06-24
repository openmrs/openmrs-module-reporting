package org.openmrs.module.reporting.data.encounter.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.EncounterProvider;
import org.openmrs.Provider;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterProviderDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates a EncounterProviderDataDefinition
 * (Only works with OpenMRS 1.9 and above)
 */
@Handler(supports=EncounterProviderDataDefinition.class, order=50)
public class EncounterProviderDataEvaluator implements EncounterDataEvaluator {

    protected final Log log = LogFactory.getLog(getClass());

    @Autowired
    private EvaluationService evaluationService;
    
    @Override
    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {

        EncounterProviderDataDefinition def = (EncounterProviderDataDefinition) definition;

        // check to make sure parameter is an encounter role (method signature is type OpenmrsMetadata so it can compile again OpenMRS 1.6.x)
        if (def.getEncounterRole() != null && !def.getEncounterRole().getClass().getName().equals("org.openmrs.EncounterRole")) {
            throw new EvaluationException("EncounterRole parameter must be of type EncounterRole");
        }

        EvaluatedEncounterData data = new EvaluatedEncounterData(definition, context);

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("ep.encounter.id", "ep.provider");
		q.from(EncounterProvider.class, "ep");
		q.whereEqual("ep.encounterRole", def.getEncounterRole());
		q.whereEncounterIn("ep.encounter.encounterId", context);

		for (Object[] result : evaluationService.evaluateToList(q, context)) {
			Integer encounterId = (Integer)result[0];
			Provider provider = (Provider)result[1];

			if (!def.isSingleProvider()) {
				List l = (List) data.getData().get(encounterId);
				if (l == null) {
					l = new ArrayList();
					data.getData().put(encounterId, l);
				}
				l.add(provider);
			}
			else {
                // If there are multiple matching providers and we are in singleProvider mode then last one wins
                if (data.getData().get(encounterId) != null) {
                    log.warn("Multiple matching providers for encounter " + encounterId + "... picking one");
                }
                data.addData(encounterId, provider);
            }
        }

		return data;
	}
}
