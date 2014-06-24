package org.openmrs.module.reporting.data.obs.evaluator;

import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.PersonToObsDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.ObsEvaluationContext;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.person.PersonIdSet;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Map;

/**
 * Evaluates a PersonToObsDataDefinition to produce a ObsData
 */
@Handler(supports=PersonToObsDataDefinition.class, order=50)
public class PersonToObsDataEvaluator implements ObsDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

    /**
     *  @should return person data for each obs in the passed context
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
			// create a new (person) evaluation context using the retrieved ids
			PersonEvaluationContext personEvaluationContext = new PersonEvaluationContext();
			personEvaluationContext.setBasePersons(new PersonIdSet(new HashSet<Integer>(convertedIds.values())));

			// evaluate the joined definition via this person context
			PersonToObsDataDefinition def = (PersonToObsDataDefinition) definition;
			EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(def.getJoinedDefinition(), personEvaluationContext);

			// now create the result set by mapping the results in the person data set to obs ids
			for (Integer obsId : convertedIds.keySet()) {
				c.addData(obsId, pd.getData().get(convertedIds.get(obsId)));
			}
		}
        return c;
    }




}
