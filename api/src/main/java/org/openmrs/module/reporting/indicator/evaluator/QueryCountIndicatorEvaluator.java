/**
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
package org.openmrs.module.reporting.indicator.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.indicator.QueryCountIndicator;
import org.openmrs.module.reporting.indicator.SimpleIndicatorResult;
import org.openmrs.module.reporting.query.IdSet;
import org.openmrs.module.reporting.query.Query;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;
import org.openmrs.module.reporting.query.obs.definition.ObsQuery;
import org.openmrs.module.reporting.query.obs.service.ObsQueryService;
import org.openmrs.module.reporting.query.person.definition.PersonQuery;
import org.openmrs.module.reporting.query.person.service.PersonQueryService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Evaluates a CohortIndicator and produces a result of all dimensions to Numeric results
 */
@Handler(supports={QueryCountIndicator.class})
public class QueryCountIndicatorEvaluator implements IndicatorEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	@Autowired
	PersonQueryService personQueryService;

	@Autowired
	CohortDefinitionService cohortDefinitionService;

	@Autowired
	EncounterQueryService encounterQueryService;

	@Autowired
	ObsQueryService obsQueryService;

	/**
	 * Default Constructor
	 */
	public QueryCountIndicatorEvaluator() {}

	/**
     * @see IndicatorEvaluator#evaluate(Indicator, EvaluationContext)
     */
    public IndicatorResult evaluate(Indicator indicator, EvaluationContext context) throws EvaluationException {

    	QueryCountIndicator qi = (QueryCountIndicator) indicator;
    	
    	SimpleIndicatorResult result = new SimpleIndicatorResult();
    	result.setContext(context);
    	result.setIndicator(qi);

		Query q = qi.getQuery().getParameterizable();

		IdSet idSet = null;

		if (q instanceof PersonQuery) {
			PersonQuery personQuery = (PersonQuery)q;
			idSet = personQueryService.evaluate(new Mapped<PersonQuery>(personQuery, qi.getQuery().getParameterMappings()), context);
		}
		else if (q instanceof CohortDefinition) {
			CohortDefinition cohortDefinition = (CohortDefinition)q;
			idSet = cohortDefinitionService.evaluate(new Mapped<CohortDefinition>(cohortDefinition, qi.getQuery().getParameterMappings()), context);
		}
		else if (q instanceof EncounterQuery) {
			EncounterQuery encounterQuery = (EncounterQuery)q;
			idSet = encounterQueryService.evaluate(new Mapped<EncounterQuery>(encounterQuery, qi.getQuery().getParameterMappings()), context);
		}
		else if (q instanceof ObsQuery) {
			ObsQuery obsQuery = (ObsQuery)q;
			idSet = obsQueryService.evaluate(new Mapped<ObsQuery>(obsQuery, qi.getQuery().getParameterMappings()), context);
		}

		if (idSet == null) {
			throw new EvaluationException(getClass().getSimpleName() + " does not support Query instances of type " + q.getClass().getName());
		}

		result.setNumeratorResult(idSet.getSize());

		return result;
    }
}