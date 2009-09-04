package org.openmrs.module.indicator.dimension;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.CohortIndicatorResult;
import org.openmrs.module.indicator.IndicatorResult;


/**
 * Represents the result of having evaluated an Indicator, and then applied some dimensions to it.
 * TODO refactor so that this actually knows about Dimensions, not just the cohort that results from evaluating them
 */
public class CohortIndicatorAndDimensionResult implements IndicatorResult<CohortIndicator> {

	private EvaluationContext context;
	private CohortIndicatorResult parentResult;
	private Map<String, Cohort> appliedCohorts = new HashMap<String, Cohort>();
	
	public CohortIndicatorAndDimensionResult(CohortIndicatorResult parentResult, EvaluationContext context) {
	    this.parentResult = parentResult;
    }

	public void applyFilter(String key, Cohort cohort) {
	    appliedCohorts.put(key, cohort);
    }

	public Number getValue() {
		if (appliedCohorts.size() > 0)
			return parentResult.getValueForSubset(appliedCohorts.values().toArray(new Cohort[appliedCohorts.size()]));
		else
			return parentResult.getValue();
    }

	public EvaluationContext getContext() {
	    return context;
    }

	public CohortIndicator getDefinition() {
	    return parentResult.getDefinition();
    }

}
