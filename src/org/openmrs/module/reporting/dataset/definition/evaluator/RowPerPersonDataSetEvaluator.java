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
package org.openmrs.module.reporting.dataset.definition.evaluator;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.RowPerObjectDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.RowPerPersonDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.query.QueryResult;
import org.openmrs.module.reporting.query.person.EvaluatedPersonQuery;
import org.openmrs.module.reporting.query.person.service.PersonQueryService;

/**
 * The logic that evaluates a {@link RowPerObjectDataSetDefinition} and produces an {@link DataSet}
 */
@Handler(supports=RowPerPersonDataSetDefinition.class)
public class RowPerPersonDataSetEvaluator extends RowPerObjectDataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Public constructor
	 */
	public RowPerPersonDataSetEvaluator() { }
	
	/**
	 * Implementations of this method should evaluate the appropriate id filters in the DataSetDefinition and
	 * populate these QueryResults within the Context
	 */
	public void populateFilterQueryResults(RowPerObjectDataSetDefinition<?> dsd, EvaluationContext context) throws EvaluationException {
		RowPerPersonDataSetDefinition rpp = (RowPerPersonDataSetDefinition) dsd;
		if (rpp.getPersonFilter() != null) {
			EvaluatedPersonQuery personQuery = Context.getService(PersonQueryService.class).evaluate(rpp.getPersonFilter(), context);
			context.getBaseQueryResults().put(Person.class, personQuery);
		}
	}
	
	/**
	 * Implementations of this method should return the base QueryResult that is appropriate for the passed DataSetDefinition
	 */
	public QueryResult getBaseQueryResult(RowPerObjectDataSetDefinition<?> dsd, EvaluationContext context) {
		EvaluatedPersonQuery s = (EvaluatedPersonQuery)context.getBaseQueryResults().get(Person.class);
		if (s == null) {
			s = new EvaluatedPersonQuery();
			String query = "select person_id from person where voided = false"; // TODO: Is this right?
			List<List<Object>> results = Context.getAdministrationService().executeSQL(query, true);
			for (List<Object> l : results) {
				s.add((Integer)l.get(0));
			}
		}
		return s;
	}
}
