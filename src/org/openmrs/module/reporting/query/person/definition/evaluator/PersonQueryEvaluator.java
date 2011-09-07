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
package org.openmrs.module.reporting.query.person.definition.evaluator;

import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.person.EvaluatedPersonQuery;
import org.openmrs.module.reporting.query.person.definition.PersonQuery;

/**
 * Each implementation of this class is expected to evaluate one or more type of PersonQuery to produce an EvaluatedPersonQuery
 */
public interface PersonQueryEvaluator {
	
	/**
	 * Evaluate an PersonQuery for the given EvaluationContext
	 */
	public EvaluatedPersonQuery evaluate(PersonQuery definition, EvaluationContext context);
}
