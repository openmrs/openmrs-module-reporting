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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.cohort.Cohorts;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition.CohortDataSetColumn;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * The logic that evaluates a {@link CohortCrossTabDataSetDefinition} and produces a {@link MapDataSet}
 * 
 * @see CohortCrossTabDataSetDefinition
 * @see MapDataSet
 */
@Handler(supports={CohortCrossTabDataSetDefinition.class})
public class CohortDataSetEvaluator implements DataSetEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Default Constructor
	 */
	public CohortDataSetEvaluator() { }
	
	/**
	 * @throws EvaluationException 
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		MapDataSet data = new MapDataSet(dataSetDefinition, context);

		CohortCrossTabDataSetDefinition dsd = (CohortCrossTabDataSetDefinition) dataSetDefinition;		
		CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
		
		for (CohortDataSetColumn col : dsd.getDataSetColumns()) {
			
			Cohort rowCohort;
			try {
				rowCohort = (col.getRowDefinition() == null ? context.getBaseCohort() : cds.evaluate(col.getRowDefinition(), context));
			} catch (Exception ex) {
				throw new EvaluationException("row definition for row=" + col.getRowName() + " , col=" + col.getColumnName(), ex);
			}
			if (rowCohort == null) {
                rowCohort = Cohorts.allPatients(context);
			}

			Cohort colCohort;
			try {
				colCohort = (col.getColumnDefinition() == null ? context.getBaseCohort() : cds.evaluate(col.getColumnDefinition(), context));
			} catch (Exception ex) {
				throw new EvaluationException("column definition for row=" + col.getRowName() + " , col=" + col.getColumnName(), ex);
			}
			if (colCohort == null) {
				colCohort = Cohorts.allPatients(context);
			}
			
			Cohort c = CohortUtil.intersect(rowCohort, colCohort);
			data.addData(col, c);
		}
		
		return data;
	}
}
