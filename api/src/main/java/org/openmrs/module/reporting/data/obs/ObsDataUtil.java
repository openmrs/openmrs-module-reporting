package org.openmrs.module.reporting.data.obs;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.query.obs.ObsQueryResult;
import org.openmrs.module.reporting.query.obs.definition.AllObsQuery;
import org.openmrs.module.reporting.query.obs.service.ObsQueryService;

import java.util.Set;

/**
 * Obs Data Utility methods
 */
public class ObsDataUtil {

    /**
     * @return the base set of obs ids relevant for the passed EvaluationContext or null for all obs ids
     * If returnNullForAllObsIds is false, then this will return all obs ids in the system if unconstrained by the context
     */
    public static Set<Integer> getObsIdsForContext(EvaluationContext context, boolean returnNullForAllObsIds) throws EvaluationException {
        boolean isConstrained = context.getBaseCohort() != null;
        if (context instanceof ObsEvaluationContext) {
            isConstrained = isConstrained || ((ObsEvaluationContext)context).getBaseObs() != null;
        }
        if (!returnNullForAllObsIds || isConstrained) {
            ObsQueryResult allIds = Context.getService(ObsQueryService.class).evaluate(new AllObsQuery(), context);
            return allIds.getMemberIds();
        }
        return null;
    }

}
