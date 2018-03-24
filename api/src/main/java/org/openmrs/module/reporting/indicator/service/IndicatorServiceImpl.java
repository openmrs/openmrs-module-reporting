/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
