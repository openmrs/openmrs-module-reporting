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
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.LabEncounterDataSet;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.LabEncounterDataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;

/**
 * The logic that evaluates a {@link LabOrderDataSetDefinition} 
 * and produces an {@link LabOrderDataSet}
 * 
 * @see LabOrderDataSetDefinition
 * @see LabOrderDataSet
 */
@Handler(supports={LabEncounterDataSetDefinition.class})
public class LabEncounterDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Public constructor
	 */
	public LabEncounterDataSetEvaluator() { }	
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet<?> evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) {
		
		if (context == null) {
			context = new EvaluationContext();
		}
		
		LabEncounterDataSetDefinition definition = 
			(LabEncounterDataSetDefinition) dataSetDefinition;
		
		Cohort cohort = context.getBaseCohort();
		
		//List<Patient> patients = 
		//	Context.getPatientSetService().getPatients(cohort.getMemberIds());		
		//List<Order> orders = 
		//	Context.getOrderService().getOrders(null, patients, null, null, null, null, null);
		
		Map<Integer, Encounter> encounterMap = 
			Context.getPatientSetService().getEncounters(cohort);
		
		List<Encounter> encounters = new Vector<Encounter>(encounterMap.values());
		
		return new LabEncounterDataSet(definition, context, encounters);		
	}

}
