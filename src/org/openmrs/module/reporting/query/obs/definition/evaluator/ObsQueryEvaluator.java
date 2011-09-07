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
package org.openmrs.module.reporting.query.obs.definition.evaluator;

import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.obs.EvaluatedObsQuery;
import org.openmrs.module.reporting.query.obs.definition.ObsQuery;

/**
 * Each implementation of this class is expected to evaluate one or more type of ObsQuery to produce an EvaluatedObsQuery
 */
public interface ObsQueryEvaluator {
	
	/**
	 * Evaluate an ObsQuery for the given EvaluationContext
	 */
	public EvaluatedObsQuery evaluate(ObsQuery definition, EvaluationContext context);
}
