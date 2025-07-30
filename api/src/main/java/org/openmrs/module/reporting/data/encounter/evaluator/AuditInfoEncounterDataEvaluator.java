/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
