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

import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.IdSet;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.AllEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.CompositionEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;
import org.openmrs.module.reporting.query.evaluator.CompositionQueryEvaluator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Evaluates an CompositionEncounterQuery and produces an IdSet
 */
@Handler(supports={CompositionEncounterQuery.class})
public class CompositionEncounterQueryEvaluator extends CompositionQueryEvaluator<EncounterQuery, Encounter> {

	@Autowired
	EncounterQueryService encounterQueryService;

	/**
	 * Default Constructor
	 */
	public CompositionEncounterQueryEvaluator() {}

	@Override
	protected IdSet<Encounter> evaluateQuery(Mapped<EncounterQuery> query, EvaluationContext context) throws EvaluationException {
		return encounterQueryService.evaluate(query, context);
	}

	@Override
	protected EncounterQuery getAllIdQuery() {
		return new AllEncounterQuery();
	}

	/**
     * @throws EvaluationException 
	 * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
     */
    public EncounterQueryResult evaluate(EncounterQuery encounterQuery, EvaluationContext context) throws EvaluationException {
		EncounterQueryResult result = new EncounterQueryResult(encounterQuery, context);
		result.setMemberIds(evaluateToIdSet(encounterQuery, context).getMemberIds());
		return result;
    }
}