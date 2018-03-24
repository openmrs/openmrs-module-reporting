/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DefinitionLibraryCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.definition.library.AllDefinitionLibraries;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Handler(supports=DefinitionLibraryCohortDefinition.class)
public class DefinitionLibraryCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    private AllDefinitionLibraries definitionLibraries;

    @Autowired
    private CohortDefinitionService cohortDefinitionService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
        DefinitionLibraryCohortDefinition def = (DefinitionLibraryCohortDefinition) cohortDefinition;
        CohortDefinition referencedDefinition = definitionLibraries.getDefinition(CohortDefinition.class, def.getDefinitionKey());

        // parameters without values explicitly defined should be mapped straight through
        Mapped<CohortDefinition> mapped = Mapped.mapStraightThrough(referencedDefinition);
        if (def.getParameterValues() != null) {
            for (Map.Entry<String, Object> e : def.getParameterValues().entrySet()) {
                mapped.addParameterMapping(e.getKey(), e.getValue());
            }
        }

        return cohortDefinitionService.evaluate(mapped, context);
    }

}
