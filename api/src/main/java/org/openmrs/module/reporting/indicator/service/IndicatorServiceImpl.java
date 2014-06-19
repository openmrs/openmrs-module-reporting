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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.IndicatorResult;

/**
 * Base Implementation of IndicatorService
 */
public class IndicatorServiceImpl extends BaseDefinitionService<Indicator> implements IndicatorService {

	protected static Log log = LogFactory.getLog(IndicatorServiceImpl.class);
	
	/**
	 * @see DefinitionService#getDefinitionType()
	 */
	public Class<Indicator> getDefinitionType() {
		return Indicator.class;
	}
	
	/** 
	 * @see IndicatorService#evaluate(Indicator, EvaluationContext)
	 */
	@Override
	public IndicatorResult evaluate(Indicator definition, EvaluationContext context) throws EvaluationException {
		return (IndicatorResult)super.evaluate(definition, context);
	}
	
	/** 
	 * @see IndicatorService#evaluate(Mapped, EvaluationContext)
	 */
	@Override
	public IndicatorResult evaluate(Mapped<? extends Indicator> definition, EvaluationContext context) throws EvaluationException {
		return (IndicatorResult) super.evaluate(definition, context);
	}
}
