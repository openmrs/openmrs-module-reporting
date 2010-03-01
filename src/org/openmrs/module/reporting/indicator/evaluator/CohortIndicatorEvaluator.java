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
    	
    	CohortIndicatorResult ind = new CohortIndicatorResult();
    	ind.setContext(context);
    	ind.setIndicator(cid);
		
		CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
		Cohort c = cds.evaluate(cid.getCohortDefinition(), context);
		
		// Ensure we are filtering on the base cohort
		if (context.getBaseCohort() != null) {
			c = Cohort.intersect(c, context.getBaseCohort());
		}
		
		// Evaluate Logic Criteria
    	if (cid.getLogicExpression() != null) {
    		try {
    			LogicCriteria criteria = Context.getLogicService().parseString(cid.getLogicExpression());
    			Map<Integer, Result> logicResults = Context.getLogicService().eval(c, criteria);
    			for (Integer memberId : logicResults.keySet()) {
    				ind.addCohortValue(memberId, logicResults.get(memberId).toNumber());
    			}
    		}
    		catch(LogicException e) {
    			throw new APIException("Error evaluating logic criteria", e);
    		}
    	}
    	// Or copy the evaluated cohort into the indicator
    	else {
    		if (c != null) { 
	    		for (Integer memberId : c.getMemberIds()) {
	    			ind.addCohortValue(memberId, 1);
	    		}
    		}
    	}
		return ind;
    }
}