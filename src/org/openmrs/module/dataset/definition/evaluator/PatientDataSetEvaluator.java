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
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.PatientDataSet;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;

/**
 *
 */
public class PatientDataSetEvaluator implements DataSetEvaluator {

	/**
	 * Logger 
	 */
	protected Log log = LogFactory.getLog(this.getClass());


	/**
	 * Public constructor
	 */
	public PatientDataSetEvaluator() { }
	
	
	/**
     * @see org.openmrs.module.dataset.evaluator.DataSetEvaluator#canEvaluate(org.openmrs.module.dataset.definition.DataSetDefinition)
     */
	public boolean canEvaluate(Class<? extends DataSetDefinition> dataSetDefinition) {
		
		log.info("DataSetDefinition: " + dataSetDefinition.getClass());

		
		return (dataSetDefinition.isAssignableFrom(EncounterDataSetDefinition.class));
	}    
	
	/**
	 * 
	 */
	public DataSet<?> evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) {
		
		PatientDataSetDefinition definition = (PatientDataSetDefinition) dataSetDefinition;

		Cohort cohort = context.getBaseCohort();
		if (cohort == null)
			Context.getService(CohortDefinitionService.class).getAllPatientsCohortDefinition();
		
		
		List<Patient> patients = new ArrayList<Patient>();
		// TODO Need to convert cohort to patients

		return new PatientDataSet(definition, context, patients);

	}



    		
	
	
}
