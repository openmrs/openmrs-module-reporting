package org.openmrs.module.reporting.data.encounter.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.encounter.EncounterDataUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.ObsForEncounterDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Evaluates a ObsForEncounterDataDefinition to produce EncounterData
 */
@Handler(supports=ObsForEncounterDataDefinition.class, order=50)
public class ObsForEncounterDataEvaluator implements EncounterDataEvaluator {

    /**
     * Logger
     */
    protected final Log log = LogFactory.getLog(getClass());

    @Autowired
    private SessionFactory sessionFactory;
    
    @Override
    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        
        ObsForEncounterDataDefinition def = (ObsForEncounterDataDefinition) definition;
        EvaluatedEncounterData data = new EvaluatedEncounterData();

        Set<Integer> encIds = EncounterDataUtil.getEncounterIdsForContext(context, false);

        // just return empty set if input set is empty
        if (encIds.size() == 0) {
            return data;
        }

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Obs.class);
        criteria.add(Restrictions.eq("voided", false));
        criteria.add(Restrictions.in("encounter.id", encIds));
        criteria.add(Restrictions.eq("concept", def.getQuestion()));
        List<Object> results = criteria.list();

        // create an entry for each encounter
        for (Integer encId : encIds) {
            if (!def.isSingleObs()) {
                data.addData(encId, new ArrayList<Obs>());
            }
            else {
                data.addData(encId, null);
            }
        }

        // now populate with actual results
        for (Object result : results) {
            Obs obs = (Obs) result;

            if (!def.isSingleObs()) {
                ((List<Obs>) data.getData().get(obs.getEncounter().getId())).add(obs);
            }
            else {

                // note that if there are multiple matching obs and we are in singleObs mode then last one wins
                if (data.getData().get(obs.getEncounter().getId()) != null) {
                    log.warn("Multiple matching obs for encounter " + obs.getEncounter() + "... picking one");
                }

                data.addData(obs.getEncounter().getId(), obs);
            }
        }

        return data;
    }
}
