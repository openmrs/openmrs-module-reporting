/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
