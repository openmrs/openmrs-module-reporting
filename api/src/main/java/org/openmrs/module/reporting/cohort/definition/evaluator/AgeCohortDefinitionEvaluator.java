/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.PatientIdSet;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.IdSet;
import org.openmrs.module.reporting.query.QueryUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Evaluates an PatientCharacteristicCohortDefinition and produces a Cohort
 */
@Handler(supports={AgeCohortDefinition.class})
public class AgeCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    EvaluationService evaluationService;

	/**
	 * Default Constructor
	 */
	public AgeCohortDefinitionEvaluator() {}

	/**
     * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
     * @should return only patients born on or before the evaluation date
     * @should return only non voided patients
     * @should return only patients in the given age range
     * @should only return patients with unknown age if specified
     */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
    	AgeCohortDefinition acd = (AgeCohortDefinition) cohortDefinition;
        context = ObjectUtil.nvl(context, new EvaluationContext());

        HqlQueryBuilder q = new HqlQueryBuilder();
        q.select("p.patientId");
        q.from(Patient.class, "p");

        // If there is a minimum age, then this means patients must have a birthdate earlier than a particular date, so a max birthdate is enforced
        // If none is specified, ensure no patients are included who are born after the effective date
        Date maxBirthdateAllowed = getDateForAge(acd.getEffectiveDate(), acd.getMinAge(), acd.getMinAgeUnit());
        q.whereLessOrEqualTo("p.birthdate", ObjectUtil.nvl(maxBirthdateAllowed, acd.getEffectiveDate()));

        if (acd.getMaxAge() != null) {
            // If there is a maximum age, then this means that patients must have a birthdate after a particular date, so a min birthdate is enforced
            // We add one to the max age, and then exclude the boundary, to account for all fractional units
            Date minBirthdateAllowed = getDateForAge(acd.getEffectiveDate(), acd.getMaxAge() + 1, acd.getMaxAgeUnit());
            q.whereGreater("p.birthdate", minBirthdateAllowed);
        }

        q.wherePatientIn("p.patientId", context);

        List<Integer> pIds = evaluationService.evaluateToList(q, Integer.class, context);
        IdSet<Patient> c = new PatientIdSet(pIds);

        if (acd.isUnknownAgeIncluded()) {
            c = QueryUtil.union(c, getPatientsWithUnknownAge(context));
        }
        else {
            c = QueryUtil.subtract(c, getPatientsWithUnknownAge(context));
        }

    	return new EvaluatedCohort(new Cohort(c.getMemberIds()), cohortDefinition, context);
    }

    protected PatientIdSet getPatientsWithUnknownAge(EvaluationContext context) {
        HqlQueryBuilder q = new HqlQueryBuilder();
        q.select("p.patientId").from(Patient.class, "p").whereNull("p.birthdate").wherePatientIn("p.patientId", context);
        List<Integer> pIds = evaluationService.evaluateToList(q, Integer.class, context);
        return new PatientIdSet(pIds);
    }

    /**
     * @return the date on which a patients birthday would need to fall in order to be on the given age as of the given effective date
     */
    protected Date getDateForAge(Date effectiveDate, Integer age, DurationUnit ageUnits) {
        Date ret = null;
        if (age != null) {
            if (effectiveDate == null) {
                effectiveDate = new Date();
            }
            if (ageUnits == null) {
                ageUnits = DurationUnit.YEARS;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(effectiveDate);
            cal.add(ageUnits.getCalendarField(), -ageUnits.getFieldQuantity() * age);
            ret = cal.getTime();
        }
        return ret;
    }
}