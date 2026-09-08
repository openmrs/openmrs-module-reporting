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
