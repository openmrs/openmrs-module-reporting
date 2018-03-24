/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.indicator.evaluator;

import org.openmrs.module.reporting.definition.evaluator.DefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.IndicatorResult;

/**
 * This interfaces provides the functionality to evaluate an Indicator and return a result.
 */
public interface IndicatorEvaluator extends DefinitionEvaluator<Indicator> {
	
	/**
	 * Evaluates an Indicator based on the passed EvaluationContext
	 * @param indicator Indicator to evaluate
	 * @param context context to use during evaluation
	 * @return an IndicatorResult representing the Indicator evaluation result
	 */
	public IndicatorResult evaluate(Indicator indicator, EvaluationContext context) throws EvaluationException;
		
}

