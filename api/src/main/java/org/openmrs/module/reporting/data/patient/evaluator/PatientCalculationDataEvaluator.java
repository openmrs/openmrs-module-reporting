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

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.CalculationProvider;
import org.openmrs.calculation.CalculationRegistration;
import org.openmrs.calculation.InvalidCalculationException;
import org.openmrs.calculation.api.CalculationRegistrationService;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientCalculationDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Map;

/**
 * Evaluator for {@link org.openmrs.module.reporting.data.patient.definition.PatientCalculationDataDefinition}
 */
@Handler(supports = PatientCalculationDataDefinition.class, order = 50)
public class PatientCalculationDataEvaluator implements PatientDataEvaluator {

	/**
	 * @see PatientDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 * @should return data generated by the calculation referenced by the definition
	 * @should throw an error if no CalculationRegistration exists on the definition
	 */
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

		PatientCalculationDataDefinition def = (PatientCalculationDataDefinition) definition;

		// fail if passed-in definition has no PatientCalculation on it
		CalculationRegistration registration =  def.getCalculationRegistration();
		if (registration == null)
			throw new EvaluationException("No PatientCalculation found on this PatientCalculationDataDefinition");

		EvaluatedPatientData c = new EvaluatedPatientData(def, context);

		// return right away if there is nothing to evaluate
		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return c;
		}

		// get the calculation provider
		CalculationProvider provider;
		try {
			provider = (CalculationProvider) Context.loadClass(registration.getProviderClassName()).newInstance();
		} catch (InstantiationException e) {
			throw new EvaluationException("Could not instantiate provider for calculation " + registration.getToken(), e);
		} catch (IllegalAccessException e) {
			throw new EvaluationException("Could not instantiate provider for calculation " + registration.getToken(), e);
		} catch (ClassNotFoundException e) {
			throw new EvaluationException("Could not instantiate provider for calculation " + registration.getToken(), e);
		}

		// get the calculation from the provider
		PatientCalculation calculation;
		try {
			calculation = (PatientCalculation) provider.getCalculation(registration.getCalculationName(), registration.getConfiguration());
		} catch (InvalidCalculationException e) {
			throw new EvaluationException("The provider could not find calculation " + registration.getToken(), e);
		}

		// evaluate the calculation
		PatientCalculationService service = Context.getService(PatientCalculationService.class);
		CalculationResultMap resultMap = service.evaluate(context.getBaseCohort().getMemberIds(), calculation, context);

		// move data into return object
		for (Map.Entry<Integer, CalculationResult> entry : resultMap.entrySet()) {
			c.addData(entry.getKey(), entry.getValue());
		}

		return c;
	}
}
