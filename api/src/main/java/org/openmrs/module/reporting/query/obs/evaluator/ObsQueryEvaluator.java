/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.obs.evaluator;

import org.openmrs.module.reporting.definition.evaluator.DefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.query.obs.ObsQueryResult;
import org.openmrs.module.reporting.query.obs.definition.ObsQuery;

/**
 * Each implementation of this class is expected to evaluate one or more type of ObsQuery to produce an ObsQueryResult
 */
public interface ObsQueryEvaluator extends DefinitionEvaluator<ObsQuery> {
	
	/**
	 * Evaluate an ObsQuery for the given EvaluationContext
	 */
	public ObsQueryResult evaluate(ObsQuery definition, EvaluationContext context) throws EvaluationException;
}
