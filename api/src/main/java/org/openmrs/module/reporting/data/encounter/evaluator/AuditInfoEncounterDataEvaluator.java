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

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Encounter;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.AuditInfo;
import org.openmrs.module.reporting.data.encounter.EncounterDataUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.AuditInfoEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Handler(supports= AuditInfoEncounterDataDefinition.class)
public class AuditInfoEncounterDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData result = new EvaluatedEncounterData(definition, context);

        Set<Integer> encIds = EncounterDataUtil.getEncounterIdsForContext(context, true);

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Encounter.class);
        if (encIds != null) {
            if (encIds.size() == 0) {
                return result;
            }
            criteria.add(Restrictions.in("id", encIds));
        }
        criteria.setProjection(Projections.projectionList()
                .add(Property.forName("dateCreated"))
                .add(Property.forName("creator"))
                .add(Property.forName("dateChanged"))
                .add(Property.forName("changedBy"))
                .add(Property.forName("voided"))
                .add(Property.forName("dateVoided"))
                .add(Property.forName("voidedBy"))
                .add(Property.forName("voidReason"))
                .add(Property.forName("encounterId")));


        for (Object[] row : (List<Object[]>) criteria.list()) {
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
