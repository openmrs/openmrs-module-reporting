/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
