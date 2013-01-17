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

import java.util.Collection;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.calculation.Calculation;
import org.openmrs.calculation.parameter.ParameterDefinition;
import org.openmrs.calculation.parameter.ParameterDefinitionSet;
import org.openmrs.calculation.parameter.SimpleParameterDefinition;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.*;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.definition.configuration.Property;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

/**
 * Utility methods for adapting Reporting to work with Calculation
 */
public class ReportingCalculationUtil {
	
	/**
	 * @return a Calculation ParameterDefinitionSet matching the reporting definition parameters
	 */
	public static ParameterDefinitionSet getParameterDefinitionSet(Definition d) {
		ParameterDefinitionSet s = new ParameterDefinitionSet();
		if (d != null && d.getParameters() != null) {
			for (Parameter p : d.getParameters()) {
				String type = p.getType().getName();
				if (p.getCollectionType() != null) {
					type = p.getCollectionType().getName() + "<" + type + ">";
				}
				s.add(new SimpleParameterDefinition(p.getName(), type, p.getLabel(), false));
			}
		}
		//Set required parameter definitions
		for (Property p : DefinitionUtil.getConfigurationProperties(d)) {
			if (p.getRequired()) {
				ParameterDefinition pd = s.getParameterByKey(p.getField().getName());
				if (pd != null) {
					pd.setRequired(p.getRequired());
				}
			}
		}
		
		return s;
	}
	
	/**
	 * @return an EvaluationContext based on the passed Calculation evaluation parameters
	 */
	public static EvaluationContext getEvaluationContextForCalculation(Collection<Integer> patientIds,
	                                                                   Map<String, Object> parameterValues,
	                                                                   PatientCalculationContext pcc) {
		
		EvaluationContext context = new EvaluationContext();
		if (pcc != null) {
			context.setEvaluationDate(pcc.getNow());
		}
		context.setBaseCohort(new Cohort(patientIds));
		context.setParameterValues(parameterValues);
		
		return context;
	}

	/**
	 * @return an appropriate CalculationResult for the given object
	 */
	public static CalculationResult constructResult(Object o, PatientCalculation calculation, PatientCalculationContext  context) {
		if (o != null) {
			if (o instanceof Collection) {
				Collection c = (Collection) o;
				ListResult lr = new ListResult();
				for (Object obj : c) {
					lr.add(constructResult(obj, calculation, context));
				}
				return lr;
			}
			else if (o instanceof Obs) {
				return new ObsResult((Obs) o, calculation, context);
			}
			else if (o instanceof Encounter) {
				return new EncounterResult((Encounter) o, calculation, context);
			}
			else {
				return new SimpleResult(o, calculation, context);
			}
		}
		return null;
	}
}
