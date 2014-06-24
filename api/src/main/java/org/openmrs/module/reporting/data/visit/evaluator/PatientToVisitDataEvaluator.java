package org.openmrs.module.reporting.data.visit.evaluator;

import org.openmrs.Cohort;
import org.openmrs.Visit;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.definition.PatientToVisitDataDefinition;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates a PatientToVisitDataDefinition to produce a VisitData
 */
@Handler(supports=PatientToVisitDataDefinition.class, order=50)
public class PatientToVisitDataEvaluator  implements VisitDataEvaluator {

    @Autowired
    EvaluationService evaluationService;

    /**
     * @see org.openmrs.module.reporting.data.visit.evaluator.VisitDataEvaluator#evaluate(org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
     * @should return patient data for each visit in the passed cohort
     */
    @Override
    public EvaluatedVisitData evaluate(VisitDataDefinition definition, EvaluationContext context) throws EvaluationException {

        EvaluatedVisitData c = new EvaluatedVisitData(definition, context);

        // create a map of visit ids -> patient ids

        HqlQueryBuilder q = new HqlQueryBuilder();
        q.select("v.visitId", "v.patient.patientId");
        q.from(Visit.class, "v");
        q.whereVisitIn("v.visitId", context);

        Map<Integer, Integer> convertedIds = evaluationService.evaluateToMap(q, Integer.class, Integer.class, context);

        if (!convertedIds.keySet().isEmpty()) {
            // Create a new (patient) evaluation context using the retrieved ids
            EvaluationContext patientEvaluationContext = new EvaluationContext();
            patientEvaluationContext.setBaseCohort(new Cohort(convertedIds.values()));

            // evaluate the joined definition via this patient context
            PatientToVisitDataDefinition def = (PatientToVisitDataDefinition) definition;
            EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def.getJoinedDefinition(), patientEvaluationContext);

            // now create the result set by mapping the results in the patient data set to visit ids
            for (Integer encId : convertedIds.keySet()) {
                c.addData(encId, pd.getData().get(convertedIds.get(encId)));
            }
        }

        return c;
    }

}
