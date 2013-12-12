package org.openmrs.module.reporting.data.encounter.evaluator;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.DataUtil;
import org.openmrs.module.reporting.data.encounter.EncounterDataUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterLocationDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

/**
 * Evaluates a EncounterLocationDataDefinition to produce EncounterData
 */
@Handler(supports=EncounterLocationDataDefinition.class, order=50)
public class EncounterLocationDataEvaluator implements EncounterDataEvaluator {


    @Autowired
    SessionFactory sessionFactory;

    /**
     * @param definition
     * @param context
     * @return EncounterData map of encounterId -> encounterLocation
     * @throws EvaluationException
     */
    @Override
    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {

        EvaluatedEncounterData data = new EvaluatedEncounterData(definition, context);

        Set<Integer> encIds = EncounterDataUtil.getEncounterIdsForContext(context, false);

        // return empty set if input set is empty
        if (encIds.size() == 0) {
            return data;
        }

        StringBuilder hql = new StringBuilder();
        hql.append("select e.encounterId, e.location from Encounter as e where e.encounterId in (:ids)");
        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());
        query.setParameterList("ids", encIds);

        DataUtil.populate(data, (List<Object[]>) query.list());
        return data;
    }
}
