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
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiParameterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.renderer.ReportTemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.Map;

@Handler(supports={MultiParameterDataSetDefinition.class})
public class MultiParameterDataSetEvaluator implements DataSetEvaluator {

	public MultiParameterDataSetEvaluator() { }

	@Autowired
	DataSetDefinitionService dataSetDefinitionService;

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
			DataSet ds;
			try {
                // in case there are parameters not specified in the iteration, we map straight through
                Mapped<DataSetDefinition> mapped = Mapped.mapStraightThrough(dsd.getBaseDefinition());

                // now override those mappings with parameter values specified in the iteration
				for (Map.Entry<String, Object> param: iteration.entrySet()) {
					mapped.addParameterMapping(param.getKey(), param.getValue());
				}
				ds = dataSetDefinitionService.evaluate(mapped, ec);
			} catch (Exception ex) {
				throw new EvaluationException("baseDefinition", ex);
			}

			Iterator<DataSetRow> iterator = ds.iterator();
			while (iterator.hasNext()) {
				DataSetRow row = new DataSetRow();
				for (Parameter p : dsd.getBaseDefinition().getParameters()) {
					Object paramValue = ds.getContext().getParameterValue(p.getName());
					String columnName = ReportTemplateRenderer.PARAMETER_PREFIX + ReportTemplateRenderer.SEPARATOR + p.getName();
					row.addColumnValue(new DataSetColumn(columnName, p.getLabelOrName(), p.getType()), paramValue);
				}
				for (Map.Entry<DataSetColumn, Object> entry: iterator.next().getColumnValues().entrySet()) {
					row.addColumnValue(entry.getKey(), entry.getValue());
				}
				ret.addRow(row);
			}
		}
		
		return ret;
	}
}
