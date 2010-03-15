/**
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
package org.openmrs.module.reporting.indicator.evaluator;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.CohortIndicatorResult;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.IndicatorResult;

/**
 * Evaluates a CohortIndicator and produces a result of all dimensions to Numeric results
 */
@Handler(supports={CohortIndicator.class})
public class CohortIndicatorEvaluator implements IndicatorEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Default Constructor
	 */
	public CohortIndicatorEvaluator() {}

	/**
     * @see IndicatorEvaluator#evaluate(Indicator, EvaluationContext)
     */
    public IndicatorResult evaluate(Indicator indicator, EvaluationContext context) {

    	CohortIndicator cid = (CohortIndicator) indicator;
    	
    	CohortIndicatorResult result = new CohortIndicatorResult();
    	result.setContext(context);
    	result.setIndicator(cid);
		
		CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
		
		// Determine Base Cohort from LocationFilter and EvaluationContext base cohort
		Cohort baseCohort = context.getBaseCohort();
		if (cid.getLocationFilter() != null) {
			Cohort locationCohort = cds.evaluate(cid.getLocationFilter(), context);
			if (baseCohort == null) {
				baseCohort = locationCohort;
			}
			else {
				baseCohort = Cohort.intersect(baseCohort, locationCohort);
			}
		}
		
		// Set Definition Denominator and further restrict base cohort
		if (cid.getDenominator() != null) {
			Cohort denominatorCohort = cds.evaluate(cid.getDenominator(), context);
			if (baseCohort != null) {
				denominatorCohort = Cohort.intersect(denominatorCohort, baseCohort);
			}
			baseCohort = new Cohort(denominatorCohort.getMemberIds());
			result.setDenominatorCohort(denominatorCohort);
		}
		
		// Definition Cohort / Numerator
		Cohort cohort = cds.evaluate(cid.getCohortDefinition(), context);
		if (baseCohort != null) {
			cohort = Cohort.intersect(cohort, baseCohort);
		}
		result.setCohort(cohort);
		
		// Evaluate Logic Criteria
    	if (cid.getLogicExpression() != null) {
    		try {
    			LogicCriteria criteria = Context.getLogicService().parseString(cid.getLogicExpression());
    			Map<Integer, Result> logicResults = Context.getLogicService().eval(cohort, criteria);
    			for (Integer memberId : logicResults.keySet()) {
    				result.addLogicResult(memberId, logicResults.get(memberId).toNumber());
    			}
    		}
    		catch(LogicException e) {
    			throw new APIException("Error evaluating logic criteria", e);
    		}
    	}

		return result;
    }
}