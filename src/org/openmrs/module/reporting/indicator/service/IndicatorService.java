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

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.indicator.dimension.Dimension;
import org.springframework.transaction.annotation.Transactional;

/**
 * Contains methods pertaining to creating/updating/deleting/retiring/registering/evaluating
 * Indicators and Dimensions.<br/>
 */
@Transactional
public interface IndicatorService extends OpenmrsService {
	
	public Indicator saveIndicator(Indicator indicator) throws APIException;
	
	public void purgeIndicator(Indicator indicator) throws APIException;

	@Transactional(readOnly = true)
	public Indicator getIndicatorByUuid(String uuid) throws APIException;
	
	@Transactional(readOnly = true)
	public List<Indicator> getAllIndicators(boolean includeRetired);
	
	@Transactional(readOnly = true)
	public List<Indicator> getIndicators(String name, boolean exactMatchOnly);
	
	/**
	 * Returns an IndicatorResult for the given Indicator and EvaluationContext
	 * @param indicator
	 * @param context
	 * @return IndicatorResult for the given Indicator and EvaluationContext
	 */
	@Transactional(readOnly = true)
	public IndicatorResult evaluate(Indicator indicator, EvaluationContext context);
	
	/**
	 * Returns an IndicatorResult for the given Mapped<Indicator> and EvaluationContext
	 * @param indicator
	 * @param context
	 * @return IndicatorResult for the given Mapped<Indicator> and EvaluationContext
	 */
	@Transactional(readOnly = true)
	public IndicatorResult evaluate(Mapped<? extends Indicator> indicator, EvaluationContext context);

	@Transactional(readOnly = true)
	public List<Class<? extends Dimension>> getDimensionTypes();
	
	/**
	 * Gets a dimension given its type and primary key
	 * 
	 * @param <T>
	 * @param type
	 * @param id
	 * @return
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	public <T extends Dimension> T getDimension(Class<T> type, Integer id) throws APIException;
	
	/**
	 * Gets a dimension given its UUID
	 * 
	 * @param uuid
	 * @return
	 * @throws APIException
	 */
	@Transactional(readOnly=true)
	public Dimension getDimensionByUuid(String uuid) throws APIException;
	
	/**
	 * Gets all dimensions (possibly including retired ones)
	 * 
	 * @param includeRetired
	 * @return
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	public List<Dimension> getAllDimensions(boolean includeRetired) throws APIException;
	
	/**
	 * Persists a dimension (either as a save or an update)
	 * 
	 * @param dimension
	 * @return
	 * @throws APIException
	 */
	@Transactional
	public Dimension saveDimension(Dimension dimension) throws APIException;
	
	/**
	 * Deletes a dimension from the database
	 * 
	 * @param dimension
	 */
	@Transactional
	public void purgeDimension(Dimension dimension);
}
