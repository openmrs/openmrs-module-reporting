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