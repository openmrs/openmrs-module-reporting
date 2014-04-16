package org.openmrs.module.reporting.data.encounter.evaluator;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.DataUtil;
import org.openmrs.module.reporting.data.encounter.EncounterDataUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterLocationDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

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
