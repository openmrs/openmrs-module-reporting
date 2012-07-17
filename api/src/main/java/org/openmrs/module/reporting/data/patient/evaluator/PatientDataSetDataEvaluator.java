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
package org.openmrs.module.reporting.data.patient.evaluator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataSetDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.DataSetRowList;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.RowPerObjectDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluates a EncounterToPatientDataDefinition to produce a PatientData
 */
@Handler(supports=PatientDataSetDataDefinition.class, order=50)
public class PatientDataSetDataEvaluator implements PatientDataEvaluator {

	/** 
	 * @see PatientDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 * @should return dataset rows for each patient in the passed cohort
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		
		// Evaluate the underlying DataSet
		EvaluatedPatientData c = new EvaluatedPatientData(definition, context);
		PatientDataSetDataDefinition def = (PatientDataSetDataDefinition)definition;
		
		RowPerObjectDataSetDefinition dsd = def.getDefinition();
		dsd.addColumn("__patientId", new PatientIdDataDefinition(), null);
		SimpleDataSet d = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(def.getDefinition(), context);

		// Aggregate the Rows on the Patient Id column
		Map<Integer, DataSetRowList> data = new LinkedHashMap<Integer, DataSetRowList>();
		for (DataSetRow r : d.getRows()) {
			Integer patientId = (Integer)r.getColumnValue("__patientId");
			DataSetRowList rowsForPatient = data.get(patientId);
			if (rowsForPatient == null) {
				rowsForPatient = new DataSetRowList();
				data.put(patientId, rowsForPatient);
			}
			r.removeColumn("__patientId");
			rowsForPatient.add(r);
		}
		
		for (Integer patientId : data.keySet()) {
			
			DataSetRowList values = data.get(patientId);
			
			if (def.getWhichValues() == TimeQualifier.LAST) {
				Collections.reverse(values);
			}
			
			int numValues = values.size();
			if (def.getNumberOfValues() != null && def.getNumberOfValues() < numValues) {
				numValues = def.getNumberOfValues();
			}
			
			if (def.getNumberOfValues() != null && def.getNumberOfValues() == 1) {
				c.addData(patientId, values.size() > 0 ? values.get(0) : null);
			}
			else {
				DataSetRowList patientRows = new DataSetRowList();
				for (int i=0; i<numValues; i++) {
					DataSetRow row = values.get(i);
					patientRows.add(row);
				}
				c.addData(patientId, patientRows);
			}
		}

		dsd.removeColumnDefinition("__patientId");
		
		return c;
	}
}
