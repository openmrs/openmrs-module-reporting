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
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.VitalStatus;
import org.openmrs.module.reporting.data.converter.BirthdateToAgeConverter;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.VitalStatusDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * Evaluates a AgeDataDefinition to produce a PersonData
 */
@Handler(supports=AgeDataDefinition.class, order=50)
public class AgeDataEvaluator implements PersonDataEvaluator {

	@Autowired
	PersonDataService personDataService;

	/** 
	 * @see PersonDataEvaluator#evaluate(PersonDataDefinition, EvaluationContext)
	 * @should return all ages for the given context
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		AgeDataDefinition add = (AgeDataDefinition)definition;
		EvaluatedPersonData ret = new EvaluatedPersonData(definition, context);

		EvaluatedPersonData birthdateData = personDataService.evaluate(new BirthdateDataDefinition(), context);
		EvaluatedPersonData vitalStatusData = personDataService.evaluate(new VitalStatusDataDefinition(), context);

		for (Map.Entry<Integer, Object> e : birthdateData.getData().entrySet()) {
			Integer pId = e.getKey();
			VitalStatus vitalStatus = (VitalStatus)vitalStatusData.getData().get(pId);
			Date effectiveAgeDate = ObjectUtil.nvl(add.getEffectiveDate(), new Date());
			Date deathDate = (vitalStatus != null ? vitalStatus.getDeathDate() : null);
			if (deathDate != null && deathDate.before(effectiveAgeDate)) {
				effectiveAgeDate = deathDate;
			}
			BirthdateToAgeConverter converter = new BirthdateToAgeConverter(effectiveAgeDate);
			ret.addData(e.getKey(), converter.convert(e.getValue()));
		}
		return ret;
	}
}
