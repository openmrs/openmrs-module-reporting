/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.person.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.query.person.PersonQueryResult;
import org.openmrs.module.reporting.query.person.definition.PatientPersonQuery;
import org.openmrs.module.reporting.query.person.definition.PersonQuery;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The logic that evaluates a {@link PatientPersonQuery} and produces an {@link PatientPersonQuery}
 */
@Handler(supports=PatientPersonQuery.class)
public class PatientPersonQueryEvaluator implements PersonQueryEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());

	@Autowired
	CohortDefinitionService cohortDefinitionService;
	
	/**
	 * Public constructor
	 */
	public PatientPersonQueryEvaluator() { }
	
	/**
	 * @see PatientPersonQueryEvaluator#evaluate(PersonQuery, EvaluationContext)
	 * @should return all of the person ids for all patients in the defined patient query
	 */
	public PersonQueryResult evaluate(PersonQuery definition, EvaluationContext context) throws EvaluationException {
		PatientPersonQuery query = (PatientPersonQuery) definition;
		EvaluatedCohort c = cohortDefinitionService.evaluate(query.getPatientQuery(), context);
		PersonQueryResult r = new PersonQueryResult(query, context);
		r.setMemberIds(c.getMemberIds());
		return r;
	}
}
