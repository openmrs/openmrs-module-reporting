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

import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for methods used to manage and evaluate EncounterQueries
 */
@Transactional
public interface EncounterQueryService extends DefinitionService<EncounterQuery> {
	
	/**
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
	@Transactional(readOnly = true)
	public EncounterQueryResult evaluate(EncounterQuery query, EvaluationContext context) throws EvaluationException;
	
	/**
	 * @see DefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	@Transactional(readOnly = true)
	public EncounterQueryResult evaluate(Mapped<? extends EncounterQuery> mappedQuery, EvaluationContext context) throws EvaluationException;
}
