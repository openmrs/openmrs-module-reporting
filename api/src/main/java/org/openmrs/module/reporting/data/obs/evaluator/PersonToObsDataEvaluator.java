package org.openmrs.module.reporting.data.obs.evaluator;

import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.ObsDataUtil;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.PersonToObsDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.module.reporting.query.person.PersonIdSet;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Evaluates a PersonToObsDataDefinition to produce a ObsData
 */
@Handler(supports=PersonToObsDataDefinition.class, order=50)
public class PersonToObsDataEvaluator implements ObsDataEvaluator {

    /**
     *  @should return person data for each obs in the passed context
     */
    @Override
    public EvaluatedObsData evaluate(ObsDataDefinition definition, EvaluationContext obsEvaluationContext) throws EvaluationException {

        DataSetQueryService dqs = Context.getService(DataSetQueryService.class);
        EvaluatedObsData c = new EvaluatedObsData(definition, obsEvaluationContext);

        Set<Integer> obsIds = ObsDataUtil.getObsIdsForContext(obsEvaluationContext, true);

        // just return empty set if input set is empty
        if (obsIds.size() == 0) {
            return c;
        }

        // create a map of obs ids -> person ids
        Map<Integer, Integer> convertedIds = dqs.convertData(Person.class, "personId", null, Obs.class, "person.personId", obsIds);

        // create a new (person) evaluation context using the retrieved ids
        PersonEvaluationContext personEvaluationContext = new PersonEvaluationContext();
        personEvaluationContext.setBasePersons(new PersonIdSet(new HashSet<Integer>(convertedIds.values())));

        // evaluate the joined definition via this person context
        PersonToObsDataDefinition def = (PersonToObsDataDefinition) definition;
        EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(def.getJoinedDefinition(), personEvaluationContext);

        // now create the result set by mapping the results in the person data set to obs ids
        for (Integer obsId : obsIds) {
            c.addData(obsId, pd.getData().get(convertedIds.get(obsId)));
        }
        return c;
    }




}
