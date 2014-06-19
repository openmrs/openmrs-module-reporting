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
