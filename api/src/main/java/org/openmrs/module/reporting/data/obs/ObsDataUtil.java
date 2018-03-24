/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
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

		// Retrieve the visits for the baseCohort if specified
		if (patIds != null) {

			HqlQueryBuilder qb = new HqlQueryBuilder();
			qb.select("o.obsId").from(Obs.class, "o").wherePatientIn("o.personId", context);
			List<Integer> obsIdsForPatIds = Context.getService(EvaluationService.class).evaluateToList(qb, Integer.class, context);

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

		// Otherwise, all visit are needed, so return appropriate value
		if (returnNullForAllObsIds) {
			return null;
		}

		HqlQueryBuilder qb = new HqlQueryBuilder().select("o.obsId").from(Obs.class, "o");
		return new HashSet<Integer>(Context.getService(EvaluationService.class).evaluateToList(qb, Integer.class, context));
	}

}
