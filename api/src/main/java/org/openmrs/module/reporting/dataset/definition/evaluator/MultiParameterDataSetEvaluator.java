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

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.DataSetRowList;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiParameterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

import java.util.Map;

@Handler(supports={MultiParameterDataSetDefinition.class})
public class MultiParameterDataSetEvaluator implements DataSetEvaluator {

	public MultiParameterDataSetEvaluator() { }

	/**
	 * @throws org.openmrs.module.reporting.evaluation.EvaluationException
	 * @see org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator#evaluate(org.openmrs.module.reporting.dataset.definition.DataSetDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @should evaluate a MultiParameterDataSetDefinition
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		
		if (context == null) {
			context = new EvaluationContext();
		}
		MultiParameterDataSetDefinition dsd = (MultiParameterDataSetDefinition) dataSetDefinition;
		SimpleDataSet ret = new SimpleDataSet(dsd, context);

		for (Map<String, Object> iteration: dsd.getIterations()) {
			EvaluationContext ec = context.shallowCopy();
			SimpleDataSet ds;
			try {
				Mapped<DataSetDefinition> mapped = new Mapped<DataSetDefinition>();
				mapped.setParameterizable(dsd.getBaseDefinition());
				for (Map.Entry<String, Object> param: iteration.entrySet()) {
					mapped.addParameterMapping(param.getKey(), param.getValue());
				}
				ds = (SimpleDataSet) Context.getService(DataSetDefinitionService.class).evaluate(mapped, ec);
			} catch (Exception ex) {
				throw new EvaluationException("baseDefinition", ex);
			}

			DataSetRowList rows = ds.getRows();
			for (DataSetRow dsRow: rows) {
				DataSetRow row = new DataSetRow();
				for (Map.Entry<String, Object> param: ds.getContext().getParameterValues().entrySet()) {
					String columnName = "param: " + param.getKey();
					row.addColumnValue(new DataSetColumn(columnName, columnName, String.class), param.getValue());
				}
				for (Map.Entry<DataSetColumn, Object> entry: dsRow.getColumnValues().entrySet()) {
					row.addColumnValue(entry.getKey(), entry.getValue());
				}
				ret.addRow(row);
			}
		}
		
		return ret;
	}
}
