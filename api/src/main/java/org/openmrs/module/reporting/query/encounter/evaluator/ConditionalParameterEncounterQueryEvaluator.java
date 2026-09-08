/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.encounter.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.ConditionalParameterEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = ConditionalParameterEncounterQuery.class)
public class ConditionalParameterEncounterQueryEvaluator implements EncounterQueryEvaluator {

    @Autowired
	EncounterQueryService encounterQueryService;

    @Override
    public EncounterQueryResult evaluate(EncounterQuery encounterQuery, EvaluationContext context) throws EvaluationException {
		ConditionalParameterEncounterQuery q = (ConditionalParameterEncounterQuery) encounterQuery;
		EncounterQueryResult ret = new EncounterQueryResult(encounterQuery, context);

		Object valueToCheck = context.getParameterValue(q.getParameterToCheck());
		Mapped<? extends EncounterQuery> match = q.getConditionalQueries().get(valueToCheck);
		if (match == null) {
			match = q.getDefaultQuery();
		}
		if (match != null) {
			EncounterQueryResult r  = encounterQueryService.evaluate(match, context);
			ret.getMemberIds().addAll(r.getMemberIds());
		}
		return ret;
    }
}
