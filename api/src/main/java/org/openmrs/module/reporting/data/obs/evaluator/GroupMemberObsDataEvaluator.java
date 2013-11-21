package org.openmrs.module.reporting.data.obs.evaluator;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.ObsDataUtil;
import org.openmrs.module.reporting.data.obs.definition.GroupMemberObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 *
 */
@Handler(supports=GroupMemberObsDataDefinition.class, order=50)
public class GroupMemberObsDataEvaluator implements ObsDataEvaluator {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public EvaluatedObsData evaluate(ObsDataDefinition definition, EvaluationContext context) throws EvaluationException {
        GroupMemberObsDataDefinition def = (GroupMemberObsDataDefinition) definition;

        EvaluatedObsData d = new EvaluatedObsData(definition, context);

        Set<Integer> obsIds = ObsDataUtil.getObsIdsForContext(context, false);

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Obs.class);
        criteria.add(Restrictions.eq("voided", false));

        // TODO finish this
        return null;
    }

}
