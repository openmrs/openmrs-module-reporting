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
package org.openmrs.module.reporting.indicator.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.reporting.indicator.dimension.Dimension;
import org.openmrs.module.reporting.indicator.evaluator.IndicatorEvaluator;
import org.openmrs.module.reporting.indicator.persister.DimensionPersister;
import org.openmrs.module.reporting.indicator.persister.IndicatorPersister;
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


	/**
	 * Returns the DimensionPersister for the passed Dimension
	 * @param dimension
	 * @return the DimensionPersister for the passed Dimension
	 * @throws APIException if no matching persister is found
	 */
	protected DimensionPersister getDimensionPersister(Class<? extends Dimension> dimension) {
		DimensionPersister persister = HandlerUtil.getPreferredHandler(DimensionPersister.class, dimension);
		if (persister == null) {
			throw new APIException("No DimensionPersister found for <" + dimension + ">");
		}
		return persister;
	}
	
	/**
	 * @see org.openmrs.module.reporting.indicator.dimension.service.DimensionService#getAllDimensions(boolean)
	 */
	public List<Dimension> getAllDimensions(boolean includeRetired) throws APIException {
		List<Dimension> ret = new ArrayList<Dimension>();
		for (DimensionPersister persister : HandlerUtil.getHandlersForType(DimensionPersister.class, null)) {
			if (log.isDebugEnabled())
				log.debug("Persister: " + persister.getClass().getName());			
			if (persister != null) { 
				ret.addAll(persister.getAllDimensions(includeRetired));
			}
		}
		return ret;
	}
	
	/**
	 * @see org.openmrs.module.reporting.indicator.dimension.service.DimensionService#getDimension(java.lang.Class, java.lang.Integer)
	 */
	public <T extends Dimension> T getDimension(Class<T> type, Integer id) throws APIException {
		DimensionPersister persister = getDimensionPersister(type);
		if (log.isDebugEnabled()) {
			log.debug("Persister: " + persister.getClass().getName());
		}
		return (T) persister.getDimension(id);
	}
	
	/**
	 * @see org.openmrs.module.reporting.indicator.dimension.service.DimensionService#getDimensionByUuid(java.lang.String)
	 */
	public Dimension getDimensionByUuid(String uuid) throws APIException {
		for (DimensionPersister p : HandlerUtil.getHandlersForType(DimensionPersister.class, null)) {
			Dimension dimension = p.getDimensionByUuid(uuid);
			if (dimension != null) {
				return dimension;
			}
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.reporting.indicator.dimension.service.DimensionService#getDimensionTypes()
	 */
	public List<Class<? extends Dimension>> getDimensionTypes() {
		List<Class<? extends Dimension>> ret = new ArrayList<Class<? extends Dimension>>();
		ret.add(CohortDefinitionDimension.class);
		return ret;
	}
	
	/**
	 * @see org.openmrs.module.reporting.indicator.dimension.service.DimensionService#purgeDimension(org.openmrs.module.reporting.indicator.dimension.Dimension)
	 */
	public void purgeDimension(Dimension dimension) {
		getDimensionPersister(dimension.getClass()).purgeDimension(dimension);
	}
	
	/**
	 * @see org.openmrs.module.reporting.indicator.dimension.service.DimensionService#saveDimension(org.openmrs.module.reporting.indicator.dimension.Dimension)
	 */
	public Dimension saveDimension(Dimension dimension) throws APIException {
		return getDimensionPersister(dimension.getClass()).saveDimension(dimension);
	}
}
