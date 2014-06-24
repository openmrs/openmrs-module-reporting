package org.openmrs.module.reporting.data.obs.evaluator;

import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.PatientToObsDataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates a PatientToObsDataDefinition to produce a ObsData
 */
@Handler(supports=PatientToObsDataDefinition.class, order=50)
public class PatientToObsDataEvaluator implements ObsDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

    /**
     *  @should return patient data for each obs in the passed context
     */
    @Override
    public EvaluatedObsData evaluate(ObsDataDefinition definition, EvaluationContext context) throws EvaluationException {

        EvaluatedObsData c = new EvaluatedObsData(definition, context);

		// create a map of obs ids -> patient ids

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("o.obsId", "o.personId");
		q.from(Obs.class, "o");
		q.whereObsIn("o.obsId", context);

		Map<Integer, Integer> convertedIds = evaluationService.evaluateToMap(q, Integer.class, Integer.class, context);

		if (!convertedIds.keySet().isEmpty()) {
			// create a new (patient) evaluation context using the retrieved ids
			EvaluationContext patientEvaluationContext = new EvaluationContext();
			patientEvaluationContext.setBaseCohort(new Cohort(convertedIds.values()));

			// evaluate the joined definition via this patient context
			PatientToObsDataDefinition def = (PatientToObsDataDefinition) definition;
			EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def.getJoinedDefinition(), patientEvaluationContext);

			// now create the result set by mapping the results in the patient data set to obs ids
			for (Integer obsId : convertedIds.keySet()) {
				c.addData(obsId, pd.getData().get(convertedIds.get(obsId)));
			}
		}

        return c;
    }
    
    
}
