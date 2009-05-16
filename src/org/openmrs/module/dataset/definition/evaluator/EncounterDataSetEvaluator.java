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

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.EncounterDataSet;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;

/**
 * The logic that evaluates a {@link EncounterDataSetDefinition} 
 * and produces an {@link EncounterDataSet}
 * 
 * @see EncounterDataSetDefinition
 * @see EncounterDataSet
 */
@Handler(supports={EncounterDataSetDefinition.class})
public class EncounterDataSetEvaluator implements DataSetEvaluator {

	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * Public constructor
	 */
	public EncounterDataSetEvaluator() { }	
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet<?> evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) {
		
		if (context == null) {
			context = new EvaluationContext();
		}
		
		EncounterDataSetDefinition definition = (EncounterDataSetDefinition) dataSetDefinition;
		Cohort cohort = context.getBaseCohort();
		if (definition.getFilter() != null) {
			Cohort tempCohort = Context.getService(CohortDefinitionService.class).evaluate(definition.getFilter(), context);
			cohort = (cohort == null ? tempCohort : Cohort.intersect(cohort, tempCohort));
		}
		
		Map<Integer, Encounter> encounterMap = Context.getPatientSetService().getEncounters(cohort);
		List<Encounter> encounters = new Vector<Encounter>(encounterMap.values());
		
		return new EncounterDataSet(definition, context, encounters);		
	}

}
