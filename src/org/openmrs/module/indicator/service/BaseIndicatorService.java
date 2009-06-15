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

import org.jfree.util.Log;
import org.openmrs.api.APIException;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.persister.DataSetDefinitionPersister;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.indicator.CohortIndicator;
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

	// Temporary storage device until we can persist indicators using serialization or the indicator DAO
	private List<Indicator> indicators = new ArrayList<Indicator>();
	
	
	/**
	 * Public constructor
	 */
	public BaseIndicatorService() { 
    	// Hard code a few indicators
    	CohortIndicator indicator1 = new CohortIndicator("DQI1", "# of patients enrolled in the HIV Program", null, null, null);
    	CohortIndicator indicator2 = new CohortIndicator("DQI2", "# of patients enrolled at the start of this month", null, null, null);
    	CohortIndicator indicator3 = new CohortIndicator("DQI3", "# of male adult patients", null, null, null);
    	CohortIndicator indicator4 = new CohortIndicator("DQI4", "# of patients with low cd4 count", null, null, null);

    	if (indicators == null) { 
    		indicators = new ArrayList<Indicator>();
    	}
    	indicators.add(indicator1);		
		indicators.add(indicator2);
		indicators.add(indicator3);
		indicators.add(indicator4);		
	}
	
	
	//***** SERVICE METHODS *****

	/**
	 * @see IndicatorService#saveIndicator(Indicator)
	 */
	public Indicator saveIndicator(Indicator indicator) throws APIException { 		
		if (indicator.getUuid() == null) { 			
			indicator.setUuid(UUID.randomUUID().toString());
			indicators.add(indicator);		
		}
		return indicator;
	}
	
	/**
	 * @see IndicatorService#saveIndicator(String)
	 */
	public void purgeIndicator(String uuid) throws APIException { 		
		for (Indicator indicator : indicators) { 
			if (indicator.getUuid().equals(uuid)) { 			
				indicators.remove(indicator);				
			}
		}						
	}
	
	/** 
	 * @see IndicatorService#getIndicatorUuid(String)
	 */
	public Indicator getIndicatorByUuid(String uuid) throws APIException {

		
		/*
		for (IndicatorPersister persister : HandlerUtil.getHandlersForType(IndicatorPersister.class, null)) {
			Indicator indicator = persister.getIndicatorByUuid(uuid);
			if (indicator != null) {
				return indicator;
			}
		}
		return null;		
		*/

		for (Indicator indicator : indicators) { 
			if (indicator.getUuid().equals(uuid))
				return indicator;			
		}				
		return null;
	}

	/** 
	 * @see IndicatorService#getAllIndicators(boolean)
	 */
	public List<Indicator> getAllIndicators(boolean includeRetired) {
		Log.info("Get all indicators");
		
		Log.info("Size: " + indicators.size());
		
		/*
		List<Indicator> indicators = new ArrayList<Indicator>();
		for (DataSetDefinitionPersister persister : HandlerUtil.getHandlersForType(IndicatorPersister.class, null)) {
			if (persister != null) { 
				indicators.addAll(persister.getAllDataSetDefinitions(includeRetired));
			}
		}*/
		return indicators;
	}

	/** 
	 * @see IndicatorService#getIndicatorByName(String, boolean)
	 */
	public List<Indicator> getIndicators(String name, boolean exactMatchOnly) {
		/*
		List<Indicator> indicators = new ArrayList<Indicator>();
		for (IndicatorPersister persister : HandlerUtil.getHandlersForType(IndicatorPersister.class, null)) {
			indicators.addAll(persister.getIndicators(name, exactMatchOnly));
		}
		return indicators;

		*/

		// TODO Temporary solution until serialization is introduced
		List<Indicator> indicatorList = new ArrayList<Indicator>();
		for (Indicator indicator : indicators) { 
			if (exactMatchOnly) { 
				if (indicator.getName().equals(name))
					indicatorList.add(indicator);
				
			}
			else { 
				// A best guess search 
				if (indicator.getName().contains(name)) { 
					indicatorList.add(indicator);
				}
			}
		}
		return indicatorList;
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

    
    /**
     * TODO persist indicators to temporary storage
     */
	public void onShutdown() {
		Log.info("Checking to see if onShutdown() gets called");
		indicators = null;
	}

	/**
	 * TODO fetch indicators from temporary storage
	 */
	public void onStartup() {		
		// this method is never called

		
		// Hard code a few indicators
    	CohortIndicator indicator1 = new CohortIndicator("DQI1", "# of patients enrolled in the HIV Program", null, null, null);
    	CohortIndicator indicator2 = new CohortIndicator("DQI2", "# of patients enrolled at the start of this month", null, null, null);
    	CohortIndicator indicator3 = new CohortIndicator("DQI3", "# of male adult patients", null, null, null);
    	CohortIndicator indicator4 = new CohortIndicator("DQI4", "# of patients with low cd4 count", null, null, null);

    	if (indicators == null) { 
    		indicators = new ArrayList<Indicator>();
    	}
    	indicators.add(indicator1);		
		indicators.add(indicator2);
		indicators.add(indicator3);
		indicators.add(indicator4);		

	}
}
