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
package org.openmrs.module.reporting.query.obs.service;

import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.obs.ObsQueryResult;
import org.openmrs.module.reporting.query.obs.definition.ObsQuery;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Base Implementation of the ObsQueryService API
 */
public class ObsQueryServiceImpl extends BaseDefinitionService<ObsQuery> implements ObsQueryService {

	/**
	 * @see DefinitionService#getDefinitionType()
	 */
	public Class<ObsQuery> getDefinitionType() {
		return ObsQuery.class;
	}
	
	/**
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 * @should evaluate an obs query
	 */
	public ObsQueryResult evaluate(ObsQuery query, EvaluationContext context) throws EvaluationException {
		return (ObsQueryResult)super.evaluate(query, context);
	}
	
	/**
	 * @see DefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	public ObsQueryResult evaluate(Mapped<? extends ObsQuery> mappedQuery, EvaluationContext context) throws EvaluationException {
		return (ObsQueryResult)super.evaluate(mappedQuery, context);
	}
}
