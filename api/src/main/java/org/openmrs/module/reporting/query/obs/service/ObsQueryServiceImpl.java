/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
