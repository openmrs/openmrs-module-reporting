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
package org.openmrs.module.reporting.definition.evaluator;

import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * This interface provides a means for specific definition implementations to plug in
 * specific implementations for how they should be persisted and retrieved.
 */
public interface DefinitionEvaluator<T extends Definition> {
	
	/**
	 * Evaluates the Passed Definition and returns an Evaluated Result
	 * @param definition definition to evaluate
	 * @param context context to use during evaluation
	 * @return the evaluated data that results from this evaluation
	 */
	public Evaluated<T> evaluate(T definition, EvaluationContext context) throws EvaluationException;

}
