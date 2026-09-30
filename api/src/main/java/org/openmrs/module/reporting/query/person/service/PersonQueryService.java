/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.person.service;

import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.person.PersonQueryResult;
import org.openmrs.module.reporting.query.person.definition.PersonQuery;
import org.openmrs.annotation.Authorized;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.api.APIException;

/**
 * Interface for methods used to manage and evaluate PersonQueries
 */
public interface PersonQueryService extends DefinitionService<PersonQuery> {
	
	/**
	 * @see DefinitionService#saveDefinition(Definition)
	 */
	@Authorized({ ReportingConstants.PRIV_MANAGE_DATA_SET_DEFINITIONS })
	public <D extends PersonQuery> D saveDefinition(D definition) throws APIException;

	/**
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
	@Authorized({ ReportingConstants.PRIV_RUN_REPORTS })
	public PersonQueryResult evaluate(PersonQuery query, EvaluationContext context) throws EvaluationException;
	
	/**
	 * @see DefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	@Authorized({ ReportingConstants.PRIV_RUN_REPORTS })
	public PersonQueryResult evaluate(Mapped<? extends PersonQuery> mappedQuery, EvaluationContext context) throws EvaluationException;
}
