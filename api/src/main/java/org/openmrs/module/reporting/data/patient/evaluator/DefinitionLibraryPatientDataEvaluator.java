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

package org.openmrs.module.reporting.data.patient.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.DefinitionLibraryPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.definition.library.AllDefinitionLibraries;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Handler(supports = DefinitionLibraryPatientDataDefinition.class)
public class DefinitionLibraryPatientDataEvaluator implements PatientDataEvaluator {

    @Autowired
    private AllDefinitionLibraries definitionLibraries;

    @Autowired
    private PatientDataService patientDataService;

    @Override
    public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
        DefinitionLibraryPatientDataDefinition def = (DefinitionLibraryPatientDataDefinition) definition;
        PatientDataDefinition referencedDefinition = definitionLibraries.getDefinition(PatientDataDefinition.class, def.getDefinitionKey());

        // parameters without values explicitly set should be mapped straight through
        Mapped<PatientDataDefinition> mapped = Mapped.mapStraightThrough(referencedDefinition);
        if (def.getParameterValues() != null) {
            for (Map.Entry<String, Object> e : def.getParameterValues().entrySet()) {
                mapped.addParameterMapping(e.getKey(), e.getValue());
            }
        }
        return patientDataService.evaluate(mapped, context);
    }

}
