/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.person;

import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.person.PersonIdSet;

import java.util.HashSet;
import java.util.Set;

/**
 * Encounter Data Utility methods
 */
public class PersonDataUtil {

	/**
	 * @return the base set of person ids relevant for the passed EvaluationContext or null for all person ids
	 * If returnNullForAllPersonIds is false, then this will return all person ids in the system if unconstrained by the context
	 */
	public static Set<Integer> getPersonIdsForContext(EvaluationContext context, boolean returnNullForAllPersonIds) {

		Set<Integer> ret = null;
		Cohort patIds = context.getBaseCohort();
		PersonIdSet personIds = (context instanceof PersonEvaluationContext ? ((PersonEvaluationContext)context).getBasePersons() : null);

		// If either context filter is not null and empty, return an empty set
		if ((patIds != null && patIds.isEmpty()) || (personIds != null && personIds.isEmpty())) {
			return new HashSet<Integer>();
		}

		if (personIds != null) {
			ret = new HashSet<Integer>(personIds.getMemberIds());
			if (patIds != null) {
				ret.retainAll(patIds.getMemberIds());
			}
		}
		else {
			if (patIds != null) {
				ret = new HashSet<Integer>(patIds.getMemberIds());
			}
		}

		// If any filter was applied, return the results of this
		if (ret != null) {
			return ret;
		}

		// Otherwise, all persons are needed, so return appropriate value
		if (returnNullForAllPersonIds) {
			return null;
		}

		SqlQueryBuilder qb = new SqlQueryBuilder("select person_id from person where voided = 0"); // Seems like a bug in core.  Using HQL here only returns persons who are associated with patients
		return new HashSet<Integer>(Context.getService(EvaluationService.class).evaluateToList(qb, Integer.class, context));
	}
}
