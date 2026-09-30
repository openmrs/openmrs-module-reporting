/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
