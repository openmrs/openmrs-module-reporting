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
package org.openmrs.module.cohort.definition.service;

import org.openmrs.Cohort;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 */
@Transactional
public interface CohortDefinitionService extends OpenmrsService {
	
	@Transactional(readOnly = true)
	public Cohort evaluate(Mapped<? extends CohortDefinition> definition, EvaluationContext evalContext) throws APIException;
	
	@Transactional(readOnly = true)
	public Cohort evaluate(CohortDefinition definition, EvaluationContext evalContext) throws APIException;
	
	@Transactional(readOnly = true)
	public <T extends CohortDefinition> T getCohortDefinition(Class<T> type, Integer id) throws APIException;
	
	@Transactional(readOnly = true)
	public CohortDefinition getCohortDefinitionByUuid(String uuid) throws APIException;

	@Transactional
	public CohortDefinition saveCohortDefinition(CohortDefinition cohortDefinition) throws APIException;
	
}

