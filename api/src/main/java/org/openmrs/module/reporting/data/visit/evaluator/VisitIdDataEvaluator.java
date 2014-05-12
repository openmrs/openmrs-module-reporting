package org.openmrs.module.reporting.data.visit.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.visit.definition.VisitIdDataDefinition;

/**
 * Evaluates a VisitIdDataDefinition to produce a VisitData
 */
@Handler(supports=VisitIdDataDefinition.class, order=50)
public class VisitIdDataEvaluator extends VisitPropertyDataEvaluator {

    @Override
    public String getPropertyName() {
        return "visitId";
    }
}
