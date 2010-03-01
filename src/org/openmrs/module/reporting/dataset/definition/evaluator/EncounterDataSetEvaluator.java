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
import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * The logic that evaluates a {@link EncounterDataSetDefinition} and produces a {@link DataSet}
 * @see EncounterDataSetDefinition
 */
@Handler(supports={EncounterDataSetDefinition.class})
public class EncounterDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Public constructor
	 */
	public EncounterDataSetEvaluator() { }	
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) {
		
		if (context == null) {
			context = new EvaluationContext();
		}
		
		List<Encounter> encounters = Context.getPatientSetService().getEncountersByForm(context.getBaseCohort(), null);

		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);
		for (Encounter e : encounters) {
			DataSetRow row = new DataSetRow();
			row.addColumnValue(EncounterDataSetDefinition.ENCOUNTER_ID, e.getEncounterId());
			row.addColumnValue(EncounterDataSetDefinition.ENCOUNTER_TYPE, e.getEncounterType().getName());
			row.addColumnValue(EncounterDataSetDefinition.FORM, (e.getForm() != null) ? e.getForm().getName() : "none");
			row.addColumnValue(EncounterDataSetDefinition.LOCATION, e.getLocation().getName());
			row.addColumnValue(EncounterDataSetDefinition.PATIENT_ID, e.getPatient().getPatientId());
			dataSet.addRow(row);
		}
		return dataSet;	
	}
}
