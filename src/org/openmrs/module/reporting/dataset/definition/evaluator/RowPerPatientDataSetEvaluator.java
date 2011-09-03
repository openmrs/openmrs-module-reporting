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
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.RowPerObjectDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.idset.IdSet;
import org.openmrs.module.reporting.idset.PatientIdSet;

/**
 * The logic that evaluates a {@link RowPerPatientDataSetDefinition} and produces an {@link DataSet}
 */
@Handler(supports=RowPerPatientDataSetDefinition.class)
public class RowPerPatientDataSetEvaluator extends RowPerObjectDataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Public constructor
	 */
	public RowPerPatientDataSetEvaluator() { }
	
	/**
	 * Implementations of this method should evaluate the appropriate id filters in the DataSetDefinition and
	 * populate these IdSets within the Context
	 */
	public void populateFilterIdSets(RowPerObjectDataSetDefinition<?> dsd, EvaluationContext context) throws EvaluationException {
		RowPerPatientDataSetDefinition rpp = (RowPerPatientDataSetDefinition) dsd;
		if (rpp.getPatientFilter() != null) {
			EvaluatedCohort filterCohort = Context.getService(CohortDefinitionService.class).evaluate(rpp.getPatientFilter(), context);
			if (context.getBaseCohort() == null) {
				context.setBaseCohort(filterCohort);
			}
			else {
				context.setBaseCohort(Cohort.intersect(context.getBaseCohort(), filterCohort));
			}
		}
	}
	
	/**
	 * Implementations of this method should return the base IdSet that is appropriate for the passed DataSetDefinition
	 */
	public IdSet getBaseIdSet(RowPerObjectDataSetDefinition<?> dsd, EvaluationContext context) {
		PatientIdSet s = new PatientIdSet();
		if (context.getBaseCohort() == null) {
			s = new PatientIdSet();
			String query = "select patient_id from patient where voided = false"; // TODO: Is this right?
			List<List<Object>> results = Context.getAdministrationService().executeSQL(query, true);
			for (List<Object> l : results) {
				s.add((Integer)l.get(0));
			}
		}
		else {
			s = new PatientIdSet(context.getBaseCohort());
		}
		return s;
	}
}
