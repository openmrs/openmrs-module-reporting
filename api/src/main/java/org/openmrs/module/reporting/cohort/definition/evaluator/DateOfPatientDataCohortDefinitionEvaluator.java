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
package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.DateOfPatientDataCohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Evaluates an DateOfPatientDataCohortDefinition and produces a Cohort
 */
@Handler(supports={DateOfPatientDataCohortDefinition.class})
public class DateOfPatientDataCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
	PatientDataService patientDataService;
	
	/**
     * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
     */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		EvaluatedCohort ret = new EvaluatedCohort(cohortDefinition, context);
		DateOfPatientDataCohortDefinition cd = (DateOfPatientDataCohortDefinition) cohortDefinition;

		PatientData patientData = patientDataService.evaluate(cd.getPatientDataDefinition(), context);
		try {
			for (Integer pId : patientData.getData().keySet()) {
				Object o = patientData.getData().get(pId);
				if (cd.getDataConverter() != null) {
					o = cd.getDataConverter().convert(o);
				}
				if (o != null) {
					Date dateToCheck = (Date)o;
					boolean datePasses = true;
					if (cd.getMinTimeInPast() != null) { // Must be at least this far in the past, so get the date of this and make it the upper bound
						Date upperBound = DateUtil.adjustDate(cd.getEffectiveDate(), -1*cd.getMinTimeInPast(), cd.getMinTimeInPastUnits());
						upperBound = DateUtil.getEndOfDayIfTimeExcluded(upperBound);
						datePasses = datePasses && upperBound.compareTo(dateToCheck) >= 0;
					}
					if (cd.getMaxTimeInPast() != null) { // Can not be any more than this in the past, so get the date of this and make the lower bound
						Date lowerBound = DateUtil.adjustDate(cd.getEffectiveDate(), -1*cd.getMaxTimeInPast(), cd.getMaxTimeInPastUnits());
						datePasses = datePasses && lowerBound.compareTo(dateToCheck) <= 0;
					}
					if (datePasses) {
						ret.addMember(pId);
					}
				}
			}
		}
		catch (Exception e) {
			throw new EvaluationException("Unable to retrieve date data from patient data definition and converter", e);
		}

		return ret;
    }
}