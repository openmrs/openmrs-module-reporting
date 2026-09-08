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
import org.openmrs.module.reporting.data.BaseData;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.DataUtil;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.data.converter.BirthdateToAgeConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.AgeAtDateOfOtherDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Date;
import java.util.List;

/**
 * Evaluates a AgeDataDefinition to produce a PersonData
 */
@Handler(supports=AgeAtDateOfOtherDataDefinition.class, order=50)
public class AgeAtDateOfOtherDataEvaluator implements PersonDataEvaluator {

	/** 
	 * @see PersonDataEvaluator#evaluate(PersonDataDefinition, EvaluationContext)
	 * @should return all ages on the date of the given definition
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
		
		AgeAtDateOfOtherDataDefinition add = (AgeAtDateOfOtherDataDefinition)definition;
		
		EvaluatedPersonData birthdates = Context.getService(PersonDataService.class).evaluate(new BirthdateDataDefinition(), context);

        MappedData<? extends DataDefinition> effectiveDateDefinition = add.getEffectiveDateDefinition();
        BaseData effectiveDates;
        if (effectiveDateDefinition.getParameterizable() instanceof PersonDataDefinition) {
            effectiveDates = Context.getService(PersonDataService.class).evaluate((MappedData<PersonDataDefinition>) effectiveDateDefinition, context);
        } else if (effectiveDateDefinition.getParameterizable() instanceof PatientDataDefinition) {
            effectiveDates = Context.getService(PatientDataService.class).evaluate((MappedData<PatientDataDefinition>) effectiveDateDefinition, context);
        } else{
            throw new EvaluationException("Can only handle PersonDataDefinition and PatientDataDefinition for effectiveDataDefinition");
        }

		List<DataConverter> converters = effectiveDateDefinition.getConverters();
		if (converters != null && converters.size() > 0) {
			for (Integer pId : effectiveDates.getData().keySet()) {
				Object convertedValue = DataUtil.convertData(effectiveDates.getData().get(pId), converters);
				effectiveDates.addData(pId, convertedValue);
			}
		}

		EvaluatedPersonData ret = new EvaluatedPersonData(definition, context);
		BirthdateToAgeConverter converter = new BirthdateToAgeConverter();
		for (Integer personId : birthdates.getData().keySet()) {
			Object birthdate = birthdates.getData().get(personId);
            Date dateOfOtherDefinition = (Date) effectiveDates.getData().get(personId);
            if (dateOfOtherDefinition != null) {
                converter.setEffectiveDate(dateOfOtherDefinition);
                ret.addData(personId, converter.convert(birthdate));
            } else {
                ret.addData(personId, null);
            }
		}

		return ret;
	}
}
