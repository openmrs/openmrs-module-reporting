/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
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
