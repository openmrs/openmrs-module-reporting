package org.openmrs.module.reporting.data.obs;

import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.obs.ObsIdSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Obs Data Utility methods
 */
public class ObsDataUtil {

	/**
	 * @return the base set of obs ids relevant for the passed EvaluationContext or null for all obs ids
	 * If returnNullForAllObsIds is false, then this will return all obs ids in the system if unconstrained by the context
	 */
	public static Set<Integer> getObsIdsForContext(EvaluationContext context, boolean returnNullForAllObsIds) {

		Cohort patIds = context.getBaseCohort();
		ObsIdSet obsIds = (context instanceof ObsEvaluationContext ? ((ObsEvaluationContext)context).getBaseObs() : null);

		// If either context filter is not null and empty, return an empty set
		if ((patIds != null && patIds.isEmpty()) || (obsIds != null && obsIds.isEmpty())) {
			return new HashSet<Integer>();
		}

		// Retrieve the obs for the baseCohort if specified
		if (patIds != null) {
			Set<Integer> obsIdsForPatIds = getObsIdsForPatients(patIds.getMemberIds());
			if (obsIds == null) {
				obsIds = new ObsIdSet(obsIdsForPatIds);
			}
			else {
				obsIds.getMemberIds().retainAll(obsIdsForPatIds);
			}
		}

		// If any filter was applied, return the results of this
		if (obsIds != null) {
			return obsIds.getMemberIds();
		}

		// Otherwise, all obs are needed, so return appropriate value
		if (returnNullForAllObsIds) {
			return null;
		}
		return getObsIdsForPatients(null);
	}

	public static Set<Integer> getObsIdsForPatients(Set<Integer> patientIds) {
		EvaluationContext context = new EvaluationContext();
		if (patientIds != null) {
			context.setBaseCohort(new Cohort(patientIds));
		}
		HqlQueryBuilder qb = new HqlQueryBuilder();
		qb.select("o.obsId").from(Obs.class, "o").wherePatientIn("o.personId", context);
		List<Integer> ids = Context.getService(EvaluationService.class).evaluateToList(qb, Integer.class);
		return new HashSet<Integer>(ids);
	}

}
