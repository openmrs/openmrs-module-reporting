package org.openmrs.module.reporting.data.encounter.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.encounter.definition.EncounterVisitDataDefinition;

@Handler(supports=EncounterVisitDataDefinition.class, order=50)
public class EncounterVisitDataEvaluator extends EncounterPropertyDataEvaluator {

    @Override
    public String getPropertyName() {
        return "visit";
    }

}
