package org.openmrs.module.reporting.data.obs.evaluator;

import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.ObsDataUtil;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.PatientToObsDataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Map;
import java.util.Set;

/**
 * Evaluates a PatientToObsDataDefinition to produce a ObsData
 */
@Handler(supports=PatientToObsDataDefinition.class, order=50)
public class PatientToObsDataEvaluator implements ObsDataEvaluator {

    /**
     *  @should return patient data for each obs in the passed cohort
     */
    public EvaluatedObsData evaluate(ObsDataDefinition definition, EvaluationContext obsEvaluationContext) throws EvaluationException {

        DataSetQueryService dqs = Context.getService(DataSetQueryService.class);
        EvaluatedObsData c = new EvaluatedObsData(definition, obsEvaluationContext);

        // create a map of obs ids -> patient ids (note assumption that personId = patientId)
        Set<Integer> obsIds = ObsDataUtil.getObsIdsForContext(obsEvaluationContext, true);
        Map<Integer, Integer> convertedIds = dqs.convertData(Person.class, "personId", null, Obs.class, "person.personId", obsIds);

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
        return c;
    }
    
    
}
