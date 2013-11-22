package org.openmrs.module.reporting.query.obs.evaluator;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.query.obs.ObsIdSet;
import org.openmrs.module.reporting.query.obs.ObsQueryResult;
import org.openmrs.module.reporting.query.obs.definition.BasicObsQuery;
import org.openmrs.module.reporting.query.obs.definition.ObsQuery;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
@Handler(supports = BasicObsQuery.class)
public class BasicObsQueryEvaluator implements ObsQueryEvaluator {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public ObsQueryResult evaluate(ObsQuery definition, EvaluationContext context) {
        context = ObjectUtil.nvl(context, new EvaluationContext());
        BasicObsQuery query = (BasicObsQuery) definition;
        ObsQueryResult queryResult = new ObsQueryResult(query, context);

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Obs.class);
        criteria.setProjection(Projections.id());
        criteria.add(Restrictions.eq("voided", false));
        if (query.getConceptList() != null) {
            criteria.add(Restrictions.in("concept", query.getConceptList()));
        }
        if (query.getOnOrAfter() != null) {
            criteria.add(Restrictions.ge("obsDatetime", query.getOnOrAfter()));
        }
        if (query.getOnOrBefore() != null) {
            criteria.add(Restrictions.le("obsDatetime", DateUtil.getEndOfDayIfTimeExcluded(query.getOnOrBefore())));
        }

        if (context.getBaseCohort() != null) {
            if (context.getBaseCohort().size() == 0) {
                return queryResult;
            } else {
                criteria.add(Restrictions.in("person.id", context.getBaseCohort().getMemberIds()));
            }
        }
        if (context instanceof ObsEvaluationContext) {
            ObsIdSet baseObs = ((ObsEvaluationContext) context).getBaseObs();
            if (baseObs != null) {
                if (baseObs.getSize() == 0) {
                    return queryResult;
                } else {
                    criteria.add(Restrictions.in("id", baseObs.getMemberIds()));
                }
            }
        }

        for (Object obsId : criteria.list()) {
            queryResult.add((Integer) obsId);
        }
        return queryResult;
    }

}
