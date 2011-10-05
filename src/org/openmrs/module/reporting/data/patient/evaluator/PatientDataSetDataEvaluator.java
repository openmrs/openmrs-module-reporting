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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataSetDataDefinition.FlattenedDataSetColumn;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataSetDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.dataset.DataSetRow;
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
		dsd.addColumn("__patientId", new PatientIdDataDefinition(), null, null);
		SimpleDataSet d = (SimpleDataSet)Context.getService(DataSetDefinitionService.class).evaluate(def.getDefinition(), context);

		// Aggregate the Rows on the Patient Id column
		Map<Integer, List<DataSetRow>> data = new LinkedHashMap<Integer, List<DataSetRow>>();
		for (DataSetRow r : d.getRows()) {
			Integer patientId = (Integer)r.getColumnValue("__patientId");
			List<DataSetRow> rowsForPatient = data.get(patientId);
			if (rowsForPatient == null) {
				rowsForPatient = new ArrayList<DataSetRow>();
				data.put(patientId, rowsForPatient);
			}
			rowsForPatient.add(r);
		}
		
		for (Integer patientId : data.keySet()) {
			
			DataSetRow retForPatient = new DataSetRow();
			
			List<DataSetRow> values = data.get(patientId);
			
			// Flatten into single row if appropriate
			if (def.getWhichValues() != null && def.getNumberOfValues() != null) {
				for (FlattenedDataSetColumn fc : def.getDataSetColumns()) {
					if (!ObjectUtil.areEqual(fc.getOriginalColumn().getName(), "__patientId")) {
						int index = def.getWhichValues() == TimeQualifier.LAST ? values.size() - fc.getIndex() - 1 : fc.getIndex();
						DataSetRow row = values.size() > fc.getIndex() ? values.get(index) : null;
						Object value = (row == null ? null : row.getColumnValue(fc.getOriginalColumn().getName()));
						retForPatient.addColumnValue(fc, value);
					}
				}
			}
			else {
				for (FlattenedDataSetColumn fc : def.getDataSetColumns()) {
					if (!ObjectUtil.areEqual(fc.getOriginalColumn().getName(), "__patientId")) {
						List<Object> l = new ArrayList<Object>();
						for (DataSetRow r : values) {
							l.add(r.getColumnValue(fc.getOriginalColumn()));
						}
						retForPatient.addColumnValue(fc, l);
					}
				}
			}
			
			c.addData(patientId, retForPatient);
		}

		dsd.removeColumnDefinition("__patientId");
		
		return c;
	}
}
