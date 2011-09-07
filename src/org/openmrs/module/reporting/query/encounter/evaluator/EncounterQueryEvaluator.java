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
package org.openmrs.module.reporting.query.encounter.evaluator;

import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.encounter.EvaluatedEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;

/**
 * Each implementation of this class is expected to evaluate one or more type of EncounterQuery to produce an EvaluatedEncounterQuery
 */
public interface EncounterQueryEvaluator {
	
	/**
	 * Evaluate an EncounterQuery for the given EvaluationContext
	 */
	public EvaluatedEncounterQuery evaluate(EncounterQuery definition, EvaluationContext context);
}
