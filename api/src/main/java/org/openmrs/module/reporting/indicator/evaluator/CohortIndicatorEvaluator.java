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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.result.Result;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.CohortIndicatorResult;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.IndicatorResult;

import java.util.Date;
import java.util.Map;

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
    public IndicatorResult evaluate(Indicator indicator, EvaluationContext context) throws EvaluationException {

    	CohortIndicator cid = (CohortIndicator) indicator;
    	
    	CohortIndicatorResult result = new CohortIndicatorResult();
    	result.setContext(context);
    	result.setIndicator(cid);
		
		CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
		
		// Determine Base Cohort from LocationFilter and EvaluationContext base cohort
		Cohort baseCohort = context.getBaseCohort();
		if (cid.getLocationFilter() != null) {
			try {
				Cohort locationCohort = cds.evaluate(cid.getLocationFilter(), context);
				if (baseCohort == null) {
					baseCohort = locationCohort;
				}
				else {
					baseCohort = Cohort.intersect(baseCohort, locationCohort);
				}
			} catch (Exception ex) {
				throw new EvaluationException("locationFilter", ex);
			}
		}
		
		// Set Definition Denominator and further restrict base cohort
		if (cid.getDenominator() != null) {
			try {
				Cohort denominatorCohort = cds.evaluate(cid.getDenominator(), context);
				if (baseCohort != null) {
					denominatorCohort = Cohort.intersect(denominatorCohort, baseCohort);
				}
				baseCohort = new Cohort(denominatorCohort.getMemberIds());
				result.setDenominatorCohort(denominatorCohort);
			} catch (Exception ex) {
				throw new EvaluationException("denominator", ex);
			}
		}
		
		// Definition Cohort / Numerator
		Cohort cohort;
		try {
			cohort = cds.evaluate(cid.getCohortDefinition(), context);
			if (baseCohort != null) {
				cohort = Cohort.intersect(cohort, baseCohort);
			}
			result.setCohort(cohort);
		} catch (Exception ex) {
			throw new EvaluationException("numerator/cohort", ex);
		}

		if (cid.getDataToAggregate() != null) {
			try {
				PatientDataService pds = Context.getService(PatientDataService.class);
				EvaluatedPatientData patientData = pds.evaluate(cid.getDataToAggregate(), context);
				for (Integer pId : patientData.getData().keySet()) {
					result.addLogicResult(pId, (Number) patientData.getData().get(pId));
				}
			}
			catch (Exception e) {
				throw new EvaluationException("dataToAggregate: " + cid.getDataToAggregate(), e);
			}
		}
		
		// Evaluate Logic Criteria
    	if (cid.getLogicExpression() != null) {
    		try {
    			LogicCriteria criteria = Context.getLogicService().parseString(cid.getLogicExpression());
    			maybeSetIndexDate(criteria, context);
    			Map<Integer, Result> logicResults = Context.getLogicService().eval(cohort, criteria);
    			for (Integer memberId : logicResults.keySet()) {
    				result.addLogicResult(memberId, logicResults.get(memberId).toNumber());
    			}
    		}
    		catch(LogicException e) {
    			throw new EvaluationException("logic expression: " + cid.getLogicExpression(), e);
    		}
    	}

		return result;
    }

	/**
     * If context has a parameter called (in order) any of [indexDate, date, endDate, startDate] then
     * the logic criteria's index date will be set to the value of that parameter.
     * 
     * Note that criteria should be a LogicCriteria. I'm using reflection so this code works on both 1.5
     * (where LogicCriteria is a class) and 1.6+ (where it's an interface)
     * 
     * @param criteria
     * @param context
     */
    private static String[] possibilities = new String[] { "indexDate", "date", "endDate", "startDate" };
    private void maybeSetIndexDate(Object criteria, EvaluationContext context) {
    	for (String p : possibilities) {
    		if (context.containsParameter(p)) {
    			Date date = (Date) context.getParameterValue(p);
    			try {
    				criteria.getClass().getMethod("asOf", Date.class).invoke(criteria, date);
    			} catch (Exception ex) {
    				throw new RuntimeException(ex);
    			}
    			return;
    		}
    	}
    }
}