package org.openmrs.module.reporting.data.patient.evaluator;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.DataUtil;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.module.reporting.query.person.PersonQueryResult;
import org.openmrs.module.reporting.query.person.definition.AllPersonQuery;
import org.openmrs.module.reporting.query.person.service.PersonQueryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

/**
 * Expects that the SQL query returns two columns, an Integer
 */
@Handler(supports= SqlPatientDataDefinition.class)
public class SqlPatientDataEvaluator implements PatientDataEvaluator {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

        SqlPatientDataDefinition def = (SqlPatientDataDefinition) definition;

        EvaluatedPatientData data = new EvaluatedPatientData(def, context);
        Set<Integer> memberIds = context.getBaseCohort().getMemberIds();
        if (memberIds != null && memberIds.size() == 0) {
            return data;
        }

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(def.getSql());
        query.setParameterList("patientIds", memberIds);

        DataUtil.populate(data, (List<Object[]>) query.list());

        return data;
    }
}
