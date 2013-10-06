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
