/*
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

package org.openmrs.module.reporting.data.encounter.evaluator;

import org.openmrs.Encounter;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.AuditInfo;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.AuditInfoEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Handler(supports= AuditInfoEncounterDataDefinition.class)
public class AuditInfoEncounterDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    @Override
    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData result = new EvaluatedEncounterData(definition, context);

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("e.dateCreated", "creator", "e.dateChanged", "changedBy");
		q.select("e.voided", "e.dateVoided", "voidedBy", "e.voidReason", "e.encounterId");
		q.from(Encounter.class, "e");
		q.leftOuterJoin("e.creator", "creator");
		q.leftOuterJoin("e.changedBy", "changedBy");
		q.leftOuterJoin("e.voidedBy", "voidedBy");
		q.whereEncounterIn("e.encounterId", context);

		for (Object[] row : evaluationService.evaluateToList(q, context)) {
			AuditInfo auditInfo = new AuditInfo();
			auditInfo.setDateCreated((Date) row[0]);
			auditInfo.setCreator((User) row[1]);
			auditInfo.setDateChanged((Date) row[2]);
			auditInfo.setChangedBy((User) row[3]);
			auditInfo.setVoided((Boolean) row[4]);
			auditInfo.setDateVoided((Date) row[5]);
			auditInfo.setVoidedBy((User) row[6]);
			auditInfo.setVoidReason((String) row[7]);
			result.addData((Integer) row[8], auditInfo);
		}

        return result;
    }
}
