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

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.IndicatorResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * Contains methods pertaining to creating/updating/deleting/retiring/registering/evaluating
 * Indicators and Dimensions.<br/>
 */
@Transactional
public interface IndicatorService extends OpenmrsService {
	
	public Indicator saveIndicator(Indicator indicator) throws APIException;	
	public void purgeIndicator(String uuid) throws APIException;
	public Indicator getIndicatorByUuid(String uuid) throws APIException;
	public List<Indicator> getAllIndicators(boolean includeRetired);
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

}
