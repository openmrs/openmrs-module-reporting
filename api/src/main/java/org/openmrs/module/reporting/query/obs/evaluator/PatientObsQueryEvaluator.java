/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.obs.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.obs.ObsDataUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.query.obs.ObsQueryResult;
import org.openmrs.module.reporting.query.obs.definition.ObsQuery;
import org.openmrs.module.reporting.query.obs.definition.PatientObsQuery;

import java.util.Set;

@Handler(supports=PatientObsQuery.class)
public class PatientObsQueryEvaluator implements ObsQueryEvaluator {

    protected Log log = LogFactory.getLog(this.getClass());

    /**
     * Public constructor
     */
    public PatientObsQueryEvaluator() { }

    /**
     * @see ObsQueryEvaluator#evaluate(ObsQuery, EvaluationContext)
     * @should return all of the obs ids for all patients in the defined patient query
     * @should filter results by patient and obs given an ObsEvaluationContext
     * @should filter results by patient given an EvaluationContext
     */
    public ObsQueryResult evaluate(ObsQuery definition, EvaluationContext context) throws EvaluationException {

        context = ObjectUtil.nvl(context, new EvaluationContext());
        PatientObsQuery query = (PatientObsQuery) definition;
        ObsQueryResult queryResult = new ObsQueryResult(query, context);

        // Calculate the patients for this query
        Cohort c = Context.getService(CohortDefinitionService.class).evaluate(query.getPatientQuery(), context);

        // Get all of the obs for all of these patients
        EvaluationContext ec = new EvaluationContext();
        ec.setBaseCohort(c);
        Set<Integer> ret = ObsDataUtil.getObsIdsForContext(ec, false);

        // Limit that to only the passed in obs if relevant
        if (context instanceof ObsEvaluationContext) {
            ObsEvaluationContext oec = (ObsEvaluationContext) context;
            if (oec.getBaseObs() != null) {
                ret.retainAll(oec.getBaseObs().getMemberIds());
            }
        }

        queryResult.setMemberIds(ret);
        return queryResult;
    }

}
