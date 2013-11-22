package org.openmrs.module.reporting.query.obs.evaluator;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.query.obs.ObsIdSet;
import org.openmrs.module.reporting.query.obs.ObsQueryResult;
import org.openmrs.module.reporting.query.obs.definition.AllObsQuery;
import org.openmrs.module.reporting.query.obs.definition.ObsQuery;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports= AllObsQuery.class)
public class AllObsQueryEvaluator implements ObsQueryEvaluator {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public ObsQueryResult evaluate(ObsQuery definition, EvaluationContext context) {
        context = ObjectUtil.nvl(context, new EvaluationContext());
        AllObsQuery query = (AllObsQuery) definition;
        ObsQueryResult queryResult = new ObsQueryResult(query, context);

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Obs.class);
        criteria.setProjection(Projections.id());
        criteria.add(Restrictions.eq("voided", false));
        if (context.getBaseCohort() != null) {
            criteria.add(Restrictions.in("person.id", context.getBaseCohort().getMemberIds()));
        }
        if (context instanceof ObsEvaluationContext) {
            ObsEvaluationContext oec = (ObsEvaluationContext) context;
            ObsIdSet baseObs = oec.getBaseObs();
            if (baseObs != null) {
                criteria.add(Restrictions.in("id", baseObs.getMemberIds()));
            }
        }
        if (context.getLimit() != null) {
            criteria.setMaxResults(context.getLimit());
        }

        for (Object o : criteria.list()) {
            queryResult.add((Integer) o);
        }
        return queryResult;
    }

}
