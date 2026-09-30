/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.encounter.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.converter.BirthdateToAgeConverter;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.AgeAtEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDatetimeDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PersonToEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

@Handler(supports=AgeAtEncounterDataDefinition.class, order=50)
public class AgeAtEncounterDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    EncounterDataService encounterDataService;

    @Override
    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EncounterDatetimeDataDefinition datetimeDataDefinition = new EncounterDatetimeDataDefinition();
        EvaluatedEncounterData encounterDatetimes = encounterDataService.evaluate(datetimeDataDefinition, context);

        PersonToEncounterDataDefinition birthdateDataDefinition = new PersonToEncounterDataDefinition(new BirthdateDataDefinition());
        EvaluatedEncounterData birthdates = encounterDataService.evaluate(birthdateDataDefinition, context);

        EvaluatedEncounterData ret = new EvaluatedEncounterData(definition, context);
        BirthdateToAgeConverter converter = new BirthdateToAgeConverter();
        for (Map.Entry<Integer, Object> entry : encounterDatetimes.getData().entrySet()) {
            Integer encId = entry.getKey();
            Object birthdate = birthdates.getData().get(encId);
            if (birthdate != null) {
                Date encounterDatetime = (Date) entry.getValue();
                converter.setEffectiveDate(encounterDatetime);
                ret.addData(encId, converter.convert(birthdate));
            }
            else {
                ret.addData(encId, null);
            }
        }
        return ret;
    }

}
