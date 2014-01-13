package org.openmrs.module.reporting.data.obs.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.ObsDataUtil;
import org.openmrs.module.reporting.data.obs.definition.GroupMemberObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 */
@Handler(supports=GroupMemberObsDataDefinition.class, order=50)
public class GroupMemberObsDataEvaluator implements ObsDataEvaluator {

    /**
     * Logger
     */
    protected final Log log = LogFactory.getLog(getClass());

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public EvaluatedObsData evaluate(ObsDataDefinition definition, EvaluationContext context) throws EvaluationException {
        GroupMemberObsDataDefinition def = (GroupMemberObsDataDefinition) definition;

        EvaluatedObsData data = new EvaluatedObsData(definition, context);

        Set<Integer> obsIds = ObsDataUtil.getObsIdsForContext(context, false);

        // return empty result if no obs to evaluate against
        if (obsIds.size() == 0) {
            return data;
        }

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Obs.class);
        criteria.add(Restrictions.eq("voided", false));
        criteria.add(Restrictions.in("obsGroup.id", obsIds));
        criteria.add(Restrictions.eq("concept", def.getQuestion()));
        List<Object> results = criteria.list();

        // create an entry for each obs
        for (Integer obsId : obsIds) {
            if (!def.isSingleObs()) {
                data.addData(obsId, new ArrayList<Obs>());
            }
            else {
                data.addData(obsId, null);
            }
        }

        // now populate with actual results
        for (Object result : results) {
            Obs obs = (Obs) result;
			new ObjectUtil().eagerInitializationObs(obs);
            if (!def.isSingleObs()) {
                ((List<Obs>) data.getData().get(obs.getObsGroup().getId())).add(obs);
            }
            else {

                // note that if there are multiple matching obs and we are in singleObs mode then last one wins
                if (data.getData().get(obs.getObsGroup().getId()) != null) {
                    log.warn("Multiple matching obs for obsgroup " + obs.getObsGroup() + "... picking one");
                }

                data.addData(obs.getObsGroup().getId(), obs);
            }
        }

        return data;
    }

}
