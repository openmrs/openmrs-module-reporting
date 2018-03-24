/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.calculation;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.parameter.ParameterDefinition;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Adapter class which exposes a Patient or Person DataDefinition as a PatientCalculation
 */
public class PatientDataCalculation extends DataCalculation implements PatientCalculation {
	
	/**
	 * Default Constructor
	 */
	public PatientDataCalculation() {
	}
	
	/**
	 * @see PatientCalculation#evaluate(Collection, Map, PatientCalculationContext)
	 * @should ui iu iu
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public CalculationResultMap evaluate(Collection<Integer> personIds, Map<String, Object> parameterValues,
	                                     PatientCalculationContext context) {
		
		EvaluationContext ec = ReportingCalculationUtil.getEvaluationContextForCalculation(personIds, parameterValues,
		    context);
		Map<Integer, Object> data = null;
		
		try {
			//Set the passed in parameter values on the definition
			if (MapUtils.isNotEmpty(parameterValues)) {
				for (ParameterDefinition p : getParameterDefinitionSet()) {
					getDataDefinition().getParameter(p.getKey()).setDefaultValue(parameterValues.get(p.getKey()));
				}
			}
			
			if (getDataDefinition() instanceof PatientDataDefinition) {
				PatientDataService service = Context.getService(PatientDataService.class);
				data = service.evaluate((PatientDataDefinition) getDataDefinition(), ec).getData();
			} else if (getDataDefinition() instanceof PersonDataDefinition) {
				PersonDataService service = Context.getService(PersonDataService.class);
				data = service.evaluate((PersonDataDefinition) getDataDefinition(), ec).getData();
			} else {
				throw new APIException(
				        "You must specify either a PersonDataDefinition or a PatientDataDefinition within a PatientCalculation");
			}
		}
		catch (EvaluationException e) {
			throw new APIException("Evaluation Exception occurred while evaluating " + getDataDefinition(), e);
		}
		
		if (data == null) {
			throw new IllegalArgumentException("No data generated while evaluating " + getDataDefinition());
		}
		
		CalculationResultMap result = new CalculationResultMap();
		for (Integer id : data.keySet()) {
			CalculationResult cr;
			if (data.get(id) instanceof Collection) {
				Collection c = (Collection) data.get(id);
				ListResult lr = new ListResult();
				for (Object obj : c) {
					lr.add(new SimpleResult(obj, this, context));
				}
				cr = lr;
			} else {
				cr = new SimpleResult(data.get(id), this, context);
			}
			
			result.put(id, cr);
		}
		
		return result;
	}
}
