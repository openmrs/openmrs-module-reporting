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
package org.openmrs.module.reporting.data.person.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluates a BirthdateDataDefinition to produce a PersonData
 */
@Handler(supports=BirthdateDataDefinition.class, order=50)
public class BirthdateDataEvaluator implements PersonDataEvaluator {

	/** 
	 * @see PersonDataEvaluator#evaluate(PersonDataDefinition, EvaluationContext)
	 * @should return all birth dates for all persons
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        Cohort baseCohort = context.getBaseCohort();
        boolean filterPatientsInQuery = baseCohort != null && baseCohort.size() < ReportingConstants.MAX_PATIENT_IDS_TO_FILTER_IN_DATABASE;
        boolean doNotFilterPatientsInJava = baseCohort == null || filterPatientsInQuery;

		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
		DataSetQueryService qs = Context.getService(DataSetQueryService.class);

        String hql = "select personId, birthdate, birthdateEstimated from Person where voided = false";
        if (filterPatientsInQuery) {
            hql += " and personId in (:patientIds)";
        }
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (filterPatientsInQuery) {
            params.put("patientIds", baseCohort);
        }

        Map<Integer, Object> ret = new LinkedHashMap<Integer, Object>();
        List<Object> queryResult = qs.executeHqlQuery(hql, params);
        for (Object o : queryResult) {
            Object[] parts = (Object[]) o;
            Integer pId = (Integer) parts[0];
            Birthdate birthdate = null;
            if (doNotFilterPatientsInJava || baseCohort.contains(pId)) {
                Date bd = (Date) parts[1];
                if (bd != null) {
                    boolean estimated = (Boolean) parts[2];
                    birthdate = new Birthdate(bd, estimated);
                }
            }
            ret.put(pId, birthdate);
        }
        c.setData(ret);
        return c;
	}

}
