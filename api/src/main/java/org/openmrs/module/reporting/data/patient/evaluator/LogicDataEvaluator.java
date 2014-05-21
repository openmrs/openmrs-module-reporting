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

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.reporting.cohort.Cohorts;
import org.openmrs.module.reporting.common.LogicUtil;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.LogicDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Map;

/**
 * Evaluates a LogicDataDefinition to produce a PatientData
 */
@Deprecated
@Handler(supports=LogicDataDefinition.class, order=50)
public class LogicDataEvaluator implements PatientDataEvaluator {

	/** 
	 * @see PatientDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 * @should return Logic Results for all patients in the context baseCohort
	 */
    @Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		
		LogicDataDefinition def = (LogicDataDefinition)definition;
		EvaluatedPatientData c = new EvaluatedPatientData(def, context);
		
		Cohort cohort = context.getBaseCohort();
        if (cohort == null) {
            cohort = Cohorts.allPatients(context);
        }

		Map<Integer, Result> m = Context.getLogicService().eval(
				cohort,
				LogicUtil.parse(def.getLogicQuery()).asOf(context.getEvaluationDate()),
				context.getParameterValues());

		c.getData().putAll(m);
		return c;
	}
}
