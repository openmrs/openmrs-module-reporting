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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.persister.DataSetDefinitionPersister;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.indicator.CohortIndicator;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.IndicatorResult;
import org.openmrs.module.indicator.evaluator.IndicatorEvaluator;
import org.openmrs.module.indicator.persister.IndicatorPersister;
import org.openmrs.util.HandlerUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base Implementation of IndicatorService
 */
@Transactional
public class BaseIndicatorService implements IndicatorService {
	
	//***** PROPERTIES *****

	private static Log log = LogFactory.getLog(BaseIndicatorService.class);
	
	/**
	 * Public constructor
	 */
	public BaseIndicatorService() { }
	
	//***** CALLBACKS *****

	public void onShutdown() { }

	public void onStartup() { }
	
	
	//***** SERVICE METHODS *****

	protected IndicatorPersister getPersister(Class<? extends Indicator> definition) {
		IndicatorPersister persister = HandlerUtil.getPreferredHandler(IndicatorPersister.class, definition);
		if (persister == null) {
			throw new APIException("No IndicatorPersister found for <" + definition + ">");
		}
		return persister;
	}	
	
	/**
	 * @see IndicatorService#saveIndicator(Indicator)
	 */
	public Indicator saveIndicator(Indicator indicator) throws APIException { 		
		return getPersister(indicator.getClass()).saveIndicator(indicator);
	}
	
	/**
	 * @see IndicatorService#saveIndicator(String)
	 */
	public void purgeIndicator(Indicator indicator) throws APIException { 
		getPersister(indicator.getClass()).purgeIndicator(indicator);
	}
	
	/** 
	 * @see IndicatorService#getIndicatorUuid(String)
	 */
	public Indicator getIndicatorByUuid(String uuid) throws APIException {		
		for (IndicatorPersister persister : HandlerUtil.getHandlersForType(IndicatorPersister.class, null)) {
			Indicator indicator = persister.getIndicatorByUuid(uuid);
			if (indicator != null) {
				return indicator;
			}
		}
		return null;		
	}

	/** 
	 * @see IndicatorService#getAllIndicators(boolean)
	 */
	public List<Indicator> getAllIndicators(boolean includeRetired) {

		List<Indicator> indicators = new ArrayList<Indicator>();
		for (IndicatorPersister persister : HandlerUtil.getHandlersForType(IndicatorPersister.class, null)) {
			if (persister != null) { 
				indicators.addAll(persister.getAllIndicators(includeRetired));
			}
		}
		return indicators;
	}

	/** 
	 * @see IndicatorService#getIndicatorByName(String, boolean)
	 */
	public List<Indicator> getIndicators(String name, boolean exactMatchOnly) {
		List<Indicator> indicators = new ArrayList<Indicator>();
		for (IndicatorPersister persister : HandlerUtil.getHandlersForType(IndicatorPersister.class, null)) {
			indicators.addAll(persister.getIndicators(name, exactMatchOnly));
		}
		return indicators;
	}	
	
	
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

    
}
