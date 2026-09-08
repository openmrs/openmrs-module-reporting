/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.library.BuiltInCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Convenience class that gives you a shorthand way of getting common cohorts of patients. Evaluation is still done
 * using the whole framework, e.g. caching is done (if you provide an EvaluationContext), and test patients are excluded
 */
public class Cohorts {

    /**
     * If your use case allows it you should always use #allPatients(EvaluationContext); this method will not cache
     * results, which can significantly impact efficiency.
     *
     * @return All non-test patients in the system (with test patients excluded per the global property
     * {@link org.openmrs.module.reporting.ReportingConstants#GLOBAL_PROPERTY_TEST_PATIENTS_COHORT_DEFINITION}
     */
    public static EvaluatedCohort allPatients() {
        return allPatients(null);
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return All non-test patients in the system (with test patients excluded per the global property
     * {@link org.openmrs.module.reporting.ReportingConstants#GLOBAL_PROPERTY_TEST_PATIENTS_COHORT_DEFINITION}
     */
    public static EvaluatedCohort allPatients(EvaluationContext context) {
        try {
            return getService().evaluate(new AllPatientsCohortDefinition(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating allPatients", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort males(EvaluationContext context) {
        try {
            return getService().evaluate(new BuiltInCohortDefinitionLibrary().getMales(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating males", e);
        }
    }

    /**
     * @param context optional (used to return a cached value if possible)
     * @return
     */
    public static EvaluatedCohort females(EvaluationContext context) {
        try {
            return getService().evaluate(new BuiltInCohortDefinitionLibrary().getFemales(), context);
        } catch (EvaluationException e) {
            throw new IllegalStateException("Error evaluating females", e);
        }
    }

    private static CohortDefinitionService getService() {
        return Context.getService(CohortDefinitionService.class);
    }

}
