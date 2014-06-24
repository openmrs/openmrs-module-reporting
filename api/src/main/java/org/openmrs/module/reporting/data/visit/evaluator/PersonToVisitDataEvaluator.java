package org.openmrs.module.reporting.data.visit.evaluator;

import org.openmrs.Cohort;
import org.openmrs.Visit;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.definition.PersonToVisitDataDefinition;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.person.PersonIdSet;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Map;

/**
 * Evaluates a PersonToVisitDataDefinition to produce a VisitData
 */
@Handler(supports=PersonToVisitDataDefinition.class, order=50)
public class PersonToVisitDataEvaluator implements VisitDataEvaluator {

    @Autowired
    EvaluationService evaluationService;

    /**
     * @see org.openmrs.module.reporting.data.visit.evaluator.VisitDataEvaluator#evaluate(org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
     * @should return person data for each visit in the passed cohort
     */
    @Override
    public EvaluatedVisitData evaluate(VisitDataDefinition definition, EvaluationContext context) throws EvaluationException {

        EvaluatedVisitData c = new EvaluatedVisitData(definition, context);

        // create a map of visit ids -> person ids

        HqlQueryBuilder q = new HqlQueryBuilder();
        q.select("v.visitId", "v.patient.patientId");
        q.from(Visit.class, "v");
        q.whereVisitIn("v.visitId", context);

        Map<Integer, Integer> convertedIds = evaluationService.evaluateToMap(q, Integer.class, Integer.class, context);

        if (!convertedIds.keySet().isEmpty()) {
            PersonEvaluationContext personEvaluationContext = new PersonEvaluationContext();
            personEvaluationContext.setBaseCohort(new Cohort(convertedIds.values()));
            personEvaluationContext.setBasePersons(new PersonIdSet(new HashSet<Integer>(convertedIds.values())));

            // evaluate the joined definition via this person context
            PersonToVisitDataDefinition def = (PersonToVisitDataDefinition) definition;
            EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(def.getJoinedDefinition(), personEvaluationContext);

            // now create the result set by mapping the results in the person data set to encounter ids
            for (Integer encId : convertedIds.keySet()) {
                c.addData(encId, pd.getData().get(convertedIds.get(encId)));
            }
        }

        return c;
    }

}
