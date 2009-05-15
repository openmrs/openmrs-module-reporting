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
package org.openmrs.module.dataset.definition.evaluator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.EncounterDataSet;
import org.openmrs.module.dataset.ObsDataSet;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.ObsDataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;

/**
 * The logic that evaluates a {@link EncounterDataSetDefinition} 
 * and produces an {@link EncounterDataSet}
 * 
 * @see EncounterDataSetDefinition
 * @see EncounterDataSet
 */
@Handler(supports={ObsDataSetDefinition.class})
public class ObsDataSetEvaluator implements DataSetEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Default Constructor
	 */
	public ObsDataSetEvaluator() { }
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet<?> evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) {
		
		if (context == null) {
			context = new EvaluationContext();
		}
		
		ObsDataSetDefinition definition = (ObsDataSetDefinition) dataSetDefinition;
		
		Cohort patients = context.getBaseCohort();
		if (definition.getFilter() != null) {
			Cohort c = Context.getService(CohortDefinitionService.class).evaluate(definition.getFilter(), context);
			patients = (patients == null ? c : Cohort.intersect(patients, c));
		}
		
		ObsDataSet ret = new ObsDataSet();
		ret.setDataSetDefinition(definition);
		ret.setEvaluationContext(context);
		List<Concept> concepts = new ArrayList<Concept>(definition.getQuestions());
		List<Obs> list = Context.getObsService().getObservations(patients, concepts, definition.getFromDate(), definition.getToDate());
		ret.setData(list);
		return ret;
	}
}
