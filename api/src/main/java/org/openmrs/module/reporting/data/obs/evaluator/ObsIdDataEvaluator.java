package org.openmrs.module.reporting.data.obs.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.ObsDataUtil;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.ObsIdDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

@Handler(supports=ObsIdDataDefinition.class, order=50)
public class ObsIdDataEvaluator extends ObsPropertyDataEvaluator {

	@Override
	public String getPropertyName() {
		return "obsId";
	}

}
