/*
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
