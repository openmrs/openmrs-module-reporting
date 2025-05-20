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

import org.openmrs.Cohort;
import org.openmrs.calculation.parameter.ParameterDefinitionSet;
import org.openmrs.calculation.parameter.SimpleParameterDefinition;
import org.openmrs.calculation.patient.PatientCalculationContext;
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
				s.getParameterByKey(p.getField().getName()).setRequired(true);
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
		if (pcc != null)
			context.setEvaluationDate(pcc.getNow());
		context.setBaseCohort(new Cohort(patientIds));
		
		return context;
	}
}
