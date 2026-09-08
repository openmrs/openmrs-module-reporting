/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.person.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.DataUtil;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates a ConvertedPersonDataDefinition
 */
@Handler(supports=ConvertedPersonDataDefinition.class, order=50)
public class ConvertedPersonDataEvaluator implements PersonDataEvaluator {

	/**
	 * @see org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @should return person data by for each patient in the passed cohort
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
		ConvertedPersonDataDefinition def = (ConvertedPersonDataDefinition)definition;
		EvaluatedPersonData unconvertedData = Context.getService(PersonDataService.class).evaluate(def.getDefinitionToConvert(), context);
		if (def.getConverters().isEmpty()) {
			c.setData(unconvertedData.getData());
		}
		else {
			for (Integer id : unconvertedData.getData().keySet()) {
				Object val = DataUtil.convertData(unconvertedData.getData().get(id), def.getConverters());
				c.addData(id, val);
			}
		}
		return c;
	}
}
