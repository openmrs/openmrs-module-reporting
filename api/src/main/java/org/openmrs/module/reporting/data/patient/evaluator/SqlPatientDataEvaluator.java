package org.openmrs.module.reporting.data.patient.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

/**
 * Expects that the SQL query returns two columns, an Integer
 */
@Handler(supports= SqlPatientDataDefinition.class)
public class SqlPatientDataEvaluator implements PatientDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

    @Override
    public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
        SqlPatientDataDefinition def = (SqlPatientDataDefinition) definition;
        EvaluatedPatientData data = new EvaluatedPatientData(def, context);

		// TODO: Support IdSetMember joining

        Set<Integer> memberIds = context.getBaseCohort().getMemberIds();
        if (memberIds != null && memberIds.size() == 0) {
            return data;
        }

		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append(def.getSql());
		for (Parameter p : definition.getParameters()) {
			q.addParameter(p.getName(), context.getParameterValue(p.getName()));
		}
		q.addParameter("patientIds", memberIds);

		Map<Integer, Object> results = evaluationService.evaluateToMap(q, Integer.class, Object.class);
		data.setData(results);

		return data;
    }
}
