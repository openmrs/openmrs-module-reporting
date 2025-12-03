/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.person.service;

import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 *  Base Implementation of the PersonDataService API
 */
public class PersonDataServiceImpl extends BaseDefinitionService<PersonDataDefinition> implements PersonDataService {

	/**
	 * @see DefinitionService#getDefinitionType()
	 */
	public Class<PersonDataDefinition> getDefinitionType() {
		return PersonDataDefinition.class;
	}
	
	/**
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 * @should evaluate a person data definition
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return new EvaluatedPersonData(definition, context);
		}
		return (EvaluatedPersonData)super.evaluate(definition, context);
	}
	
	/**
	 * @see DefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	public EvaluatedPersonData evaluate(Mapped<? extends PersonDataDefinition> mappedDefinition, EvaluationContext context) throws EvaluationException {
		return (EvaluatedPersonData)super.evaluate(mappedDefinition, context);
	}
}
