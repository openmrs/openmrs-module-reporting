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

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.encounter.EncounterDataUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.SimultaneousEncountersDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@Handler(supports= SimultaneousEncountersDataDefinition.class, order=50)
public class SimultaneousEncountersDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData results = new EvaluatedEncounterData(definition, context);

        Set<Integer> encIds = EncounterDataUtil.getEncounterIdsForContext(context, true);

        SimultaneousEncountersDataDefinition def = (SimultaneousEncountersDataDefinition) definition;

        String hql = "select enc.id, other " +
                "from Encounter enc, Encounter other " +
                "where enc.encounterDatetime = other.encounterDatetime " +
                "  and enc.patient.id = other.patient.id " +
                "  and enc.id != other.id " +
                "  and enc.voided = false " +
                "  and other.voided = false ";
        if (def.getEncounterTypeList() != null) {
            if (def.getEncounterTypeList().isEmpty()) {
                return results;
            }
            hql += "  and other.encounterType in (:encounterTypes) ";
        }
        if (encIds != null) {
            if (encIds.size() == 0) {
                // just return empty set if input set empty
                return results;
            }
            hql += "  and enc.id in (:encIds) ";
        }
        hql += "order by other.dateCreated asc"; // use the most-recently-entered encounter
        // hql += "order by ABS(other.dateCreated - enc.dateCreated) desc"; // use the encounter with the nearest datetime to enc

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        if (def.getEncounterTypeList() != null) {
            query.setParameterList("encounterTypes", def.getEncounterTypeList());
        }
        if (encIds != null) {
            query.setParameterList("encIds", encIds);
        }

        for (Object[] row : (List<Object[]>) query.list()) {
            results.addData((Integer) row[0], row[1]);
        }

        return results;
    }

}
