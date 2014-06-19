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
