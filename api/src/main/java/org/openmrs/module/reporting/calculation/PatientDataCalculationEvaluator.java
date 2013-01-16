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
package org.openmrs.module.reporting.calculation;

import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationEvaluator;
import org.openmrs.calculation.result.CohortResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates a {@link PatientDataCalculation} to produce a Result
 */
@Handler(supports=PatientDataCalculation.class, order=50)
public class PatientDataCalculationEvaluator implements PatientCalculationEvaluator {

	/**
	 * @see PatientCalculationEvaluator#evaluate(Cohort, PatientCalculation, Map, PatientCalculationContext)
	 */
	public CohortResult evaluate(Cohort cohort, PatientCalculation calculation, Map<String, Object> parameterValues, PatientCalculationContext context) {
		
		PatientDataCalculation pdc = (PatientDataCalculation) calculation;
		EvaluationContext ec = ReportingCalculationUtil.getEvaluationContextForCalculation(cohort, parameterValues, context);
		Map<Integer, Object> data = null;
		
		try {
			if (pdc.getDataDefinition() instanceof PatientDataDefinition) {
				PatientDataService service = Context.getService(PatientDataService.class);
				data = service.evaluate((PatientDataDefinition)pdc.getDataDefinition(), ec).getData();
			}
			else if (pdc.getDataDefinition() instanceof PersonDataDefinition) {
				PersonDataService service = Context.getService(PersonDataService.class);
				data = service.evaluate((PersonDataDefinition)pdc.getDataDefinition(), ec).getData();
			}
		}
		catch (EvaluationException e) {
			throw new APIException("Evaluation Exception occurred while evaluating " + pdc.getDataDefinition(), e);
		}
		
		if (data == null) {
			throw new IllegalArgumentException("You must specify either a PersonDataDefinition or a PatientDataDefinition within a PatientCalculation");
		}

		CohortResult result = new CohortResult();
		for (Integer id : data.keySet()) {
			Object value = data.get(id);
			if (pdc.getConverters() != null) {
				for (DataConverter c : pdc.getConverters()) {
					value = c.convert(value);
				}
			}
			result.put(id, new SimpleResult(value, calculation, context)); // TODO: Is SimpleResult the right thing to use here?
		}

		return result;
	}
}
