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
package org.openmrs.module.reporting.report.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.definition.util.CohortFilter;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * The default implementation of a {@link ReportDefinitionEvaluator} that
 * returns ReportData that contains a single DataSet for each DataSetDefinition
 * that is configured within it
 */
@Handler(supports={ReportDefinition.class})
public class DefaultReportDefinitionEvaluator implements ReportDefinitionEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	@Autowired
	DataSetDefinitionService dataSetDefinitionService;

	/**
	 * Default Constructor
	 */
	public DefaultReportDefinitionEvaluator() { }

	/**
	 * Evaluates each DataSetDefinition defined in the ReportDefinition and returns these within ReportData
	 * @see ReportDefinitionEvaluator#evaluate(ReportDefinition, EvaluationContext)
	 */
	@Override
	public ReportData evaluate(ReportDefinition reportDefinition, EvaluationContext context) throws EvaluationException {

		log.debug("Evaluating report: " + reportDefinition + "(" + context.getParameterValues() + ")");

		context = ObjectUtil.nvl(context, new EvaluationContext());
		ReportData data = new ReportData(reportDefinition, context);

		// We do this first so that we do not clear the cache between data sets if not necessary
		EvaluationContext dataSetContext = context.shallowCopy();
		try {
			if (reportDefinition.getBaseCohortDefinition() != null) {
				Cohort newCohort = CohortFilter.filter(dataSetContext, reportDefinition.getBaseCohortDefinition());
				dataSetContext.setBaseCohort(newCohort);
			}
		}
		catch (Exception ex) {
			throw new EvaluationException("baseCohort", ex);
		}

		Map<String, Mapped<? extends DataSetDefinition>> dataSetDefinitions = reportDefinition.getDataSetDefinitions();
		if (dataSetDefinitions != null) {
			for (String key : dataSetDefinitions.keySet()) {
				Mapped<? extends DataSetDefinition> mappedDataSetDefinition = dataSetDefinitions.get(key);
				EvaluationContext childContext = EvaluationContext.cloneForChild(dataSetContext, mappedDataSetDefinition);
				try {
					DataSet ds = dataSetDefinitionService.evaluate(mappedDataSetDefinition.getParameterizable(), childContext);
					data.getDataSets().put(key, ds);
				}
				catch (Exception ex) {
					throw new EvaluationException("data set '" + key + "'", ex);
				}
			}
		}
		
		return data;
	}
}
