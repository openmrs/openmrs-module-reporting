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
package org.openmrs.module.reporting.indicator.dimension.evaluator;

import org.openmrs.module.reporting.definition.evaluator.DefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.indicator.dimension.Dimension;

/**
 * This interfaces provides the functionality to evaluate a dimension and return a result.
 */
public interface DimensionEvaluator extends DefinitionEvaluator<Dimension> {
	
	/**
	 * Evaluates an Dimension based on the passed EvaluationContext
	 * @param dimension Dimension to evaluate
	 * @param context context to use during evaluation
	 * @return an Evaluated<Dimension> representing the Dimension evaluation result
	 */
	public Evaluated<Dimension> evaluate(Dimension dimension, EvaluationContext context) throws EvaluationException;
		
}

