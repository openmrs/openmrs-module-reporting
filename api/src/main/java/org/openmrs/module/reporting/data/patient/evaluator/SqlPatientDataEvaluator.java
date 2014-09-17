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

/**
 * Expects that the SQL query returns two columns:
 *   the first should be an Integer representing patientId
 *   the second should be the data you wish to retrieve for each patient
 * Expects that you use "patientIds" within your query to limit by the base cohort in the evaluation context:
 *   eg. "select date_created from patient where patient_id in (:patientIds)"
 */
@Handler(supports= SqlPatientDataDefinition.class)
public class SqlPatientDataEvaluator implements PatientDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

    @Override
    public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
        SqlPatientDataDefinition def = (SqlPatientDataDefinition) definition;
        EvaluatedPatientData data = new EvaluatedPatientData(def, context);

		if (context.getBaseCohort() == null || context.getBaseCohort().size() == 0) {
			return data;
		}

		SqlQueryBuilder q = new SqlQueryBuilder();
		q.append(def.getSql());
		for (Parameter p : definition.getParameters()) {
			q.addParameter(p.getName(), context.getParameterValue(p.getName()));
		}
		q.addParameter("patientIds", context.getBaseCohort());

		Map<Integer, Object> results = evaluationService.evaluateToMap(q, Integer.class, Object.class, context);
		data.setData(results);

		return data;
    }
}
