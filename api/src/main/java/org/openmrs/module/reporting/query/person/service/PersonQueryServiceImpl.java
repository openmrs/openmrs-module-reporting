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

import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.person.PersonQueryResult;
import org.openmrs.module.reporting.query.person.definition.PersonQuery;

/**
 *  Base Implementation of the PersonQueryService API
 */
public class PersonQueryServiceImpl extends BaseDefinitionService<PersonQuery> implements PersonQueryService {

	/**
	 * @see DefinitionService#getDefinitionType()
	 */
	public Class<PersonQuery> getDefinitionType() {
		return PersonQuery.class;
	}
	
	/**
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 * @should evaluate a person query
	 */
	public PersonQueryResult evaluate(PersonQuery query, EvaluationContext context) throws EvaluationException {
		return (PersonQueryResult)super.evaluate(query, context);
	}
	
	/**
	 * @see DefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	public PersonQueryResult evaluate(Mapped<? extends PersonQuery> mappedQuery, EvaluationContext context) throws EvaluationException {
		return (PersonQueryResult)super.evaluate(mappedQuery, context);
	}
}
