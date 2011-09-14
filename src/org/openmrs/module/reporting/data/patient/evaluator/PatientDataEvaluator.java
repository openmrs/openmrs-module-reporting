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
package org.openmrs.module.reporting.data.patient.evaluator;

import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.evaluator.DefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Each implementation of this class is expected to evaluate one or more type of PatientDataDefinition to produce a PatientData result
 */
public interface PatientDataEvaluator extends DefinitionEvaluator<PatientDataDefinition> {
	
	/**
	 * Evaluate an PatientDataDefinition for the given EvaluationContext
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context);
}
