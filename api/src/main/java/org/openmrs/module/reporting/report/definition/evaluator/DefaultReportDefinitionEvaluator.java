/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
