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
package org.openmrs.module.reporting.idset.service;

import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.idset.EvaluatedIdSet;
import org.openmrs.module.reporting.idset.definition.IdSetDefinition;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for methods used to manage and evaluate CohortDefinitions
 */
@Transactional
public interface IdSetDefinitionService extends DefinitionService<IdSetDefinition> {
	
	/**
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
	@Transactional(readOnly = true)
	public EvaluatedIdSet evaluate(IdSetDefinition definition, EvaluationContext context) throws EvaluationException;
	
	/**
	 * @see DefinitionService#evaluate(Mapped<Definition>, EvaluationContext)
	 */
	@Transactional(readOnly = true)
	public EvaluatedIdSet evaluate(Mapped<? extends IdSetDefinition> definition, EvaluationContext context) throws EvaluationException;
}
