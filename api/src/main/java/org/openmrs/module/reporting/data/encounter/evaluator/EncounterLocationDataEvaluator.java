package org.openmrs.module.reporting.data.encounter.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.encounter.definition.EncounterLocationDataDefinition;

/**
 * Evaluates a EncounterLocationDataDefinition to produce EncounterData
 */
@Handler(supports=EncounterLocationDataDefinition.class, order=50)
public class EncounterLocationDataEvaluator  extends EncounterPropertyDataEvaluator {

	@Override
	public String getPropertyName() {
		return "location";
	}
}
