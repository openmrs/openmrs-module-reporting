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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
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
			
			Cohort c = Cohort.intersect(rowCohort, colCohort);
			data.addData(col, c);
		}
		
		return data;
	}
}
