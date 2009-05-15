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
package org.openmrs.module.indicator.service;

import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.IndicatorResult;
import org.openmrs.module.indicator.evaluator.IndicatorEvaluator;
import org.openmrs.module.indicator.service.dao.IndicatorDAO;
import org.openmrs.util.HandlerUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base Implementation of IndicatorService
 */
@Transactional
public class BaseIndicatorService implements IndicatorService {
	
	//***** PROPERTIES *****
	private IndicatorDAO indicatorDAO;
	
	//***** SERVICE METHODS *****
	
	/** 
	 * @see IndicatorService#evaluate(Indicator, EvaluationContext)
	 */
	public IndicatorResult evaluate(Indicator indicator, EvaluationContext context) {
		IndicatorEvaluator evaluator = HandlerUtil.getPreferredHandler(IndicatorEvaluator.class, indicator.getClass());
		return evaluator.evaluate(indicator, context);
	}
	
	/** 
	 * @see IndicatorService#evaluate(Mapped, EvaluationContext)
	 */
	public IndicatorResult evaluate(Mapped<? extends Indicator> indicator, EvaluationContext context) {
		EvaluationContext childContext = EvaluationContext.cloneForChild(context, indicator);
		return evaluate(indicator.getParameterizable(), childContext);
	}

	//***** PROPERTY ACCESS *****

	/**
     * @return the indicatorDAO
     */
    public IndicatorDAO getIndicatorDAO() {
    	return indicatorDAO;
    }
	
    /**
     * @param indicatorDAO the indicatorDAO to set
     */
    public void setIndicatorDAO(IndicatorDAO indicatorDAO) {
    	this.indicatorDAO = indicatorDAO;
    }
}
