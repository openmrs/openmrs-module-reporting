package org.openmrs.module.reporting.data.encounter.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.encounter.EncounterDataUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterProviderDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Evaluates a EncounterProviderDataDefinition
 * (Only works with OpenMRS 1.9 and above)
 */
@Handler(supports=EncounterProviderDataDefinition.class, order=50)
public class EncounterProviderDataEvaluator implements EncounterDataEvaluator {


    /**
     * Logger
     */
    protected final Log log = LogFactory.getLog(getClass());


    @Autowired
    private SessionFactory sessionFactory;
    
    @Override
    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {

        EncounterProviderDataDefinition def = (EncounterProviderDataDefinition) definition;

        // check to make sure parameter is an encounter role (method signature is type OpenmrsMetadata so it can compile again OpenMRS 1.6.x)
        if (def.getEncounterRole() != null && !def.getEncounterRole().getClass().getName().equals("org.openmrs.EncounterRole")) {
            throw new EvaluationException("EncounterRole parameter must be of type EncounterRole");
        }

        EvaluatedEncounterData data = new EvaluatedEncounterData(definition, context);

        Set<Integer> encIds = EncounterDataUtil.getEncounterIdsForContext(context, false);

        // just return empty set if input set is empty
        if (encIds.size() == 0) {
            return data;
        }

        StringBuilder hql = new StringBuilder();
        hql.append("select ep.encounter.id, ep.provider from EncounterProvider as ep ");
        hql.append("where ep.encounter.id in (:ids) and ep.voided='false' ");
        
        if (def.getEncounterRole() != null) {
            hql.append("and ep.encounterRole.id = " + def.getEncounterRole().getId());
        }

        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());
        query.setParameterList("ids", encIds);

        // create an entry for each encounter
        for (Integer encId : encIds) {
            if (!def.isSingleProvider()) {
                data.addData(encId, new ArrayList<OpenmrsMetadata>());
            }
            else {
                data.addData(encId, null);
            }
        }

        // now populate with actual results
        for (Object r : query.list()) {

            Object[] result = (Object []) r;

            if (!def.isSingleProvider()) {
                ((List<OpenmrsMetadata>) data.getData().get(result[0])).add((OpenmrsMetadata) result[1]);
            }
            else {

                // note that if there are multiple matching providers and we are in singleProvider mode then last one wins
                if (data.getData().get(result[0]) != null) {
                    log.warn("Multiple matching providers for encounter " + result[0] + "... picking one");
                }

                data.addData((Integer) result[0], result[1]);
            }
        }

        return data;
    }
}
