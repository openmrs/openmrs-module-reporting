/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.encounter.service;

import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;

/**
 *  Base Implementation of the EncounterQueryService API
 */
public class EncounterQueryServiceImpl extends BaseDefinitionService<EncounterQuery> implements EncounterQueryService {

	/**
	 * @see DefinitionService#getDefinitionType()
	 */
	public Class<EncounterQuery> getDefinitionType() {
		return EncounterQuery.class;
	}
	
	/**
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 * @should evaluate an encounter query
	 */
	public EncounterQueryResult evaluate(EncounterQuery query, EvaluationContext context) throws EvaluationException {
		return (EncounterQueryResult)super.evaluate(query, context);
	}
	
	/**
	 * @see DefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	public EncounterQueryResult evaluate(Mapped<? extends EncounterQuery> mappedQuery, EvaluationContext context) throws EvaluationException {
		return (EncounterQueryResult)super.evaluate(mappedQuery, context);
	}
}
