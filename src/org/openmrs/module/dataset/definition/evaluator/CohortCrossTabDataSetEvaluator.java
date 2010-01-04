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
package org.openmrs.module.dataset.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.MapDataSet;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.dataset.definition.CohortDataSetDefinition;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.evaluation.EvaluationContext;

/**
 * The logic that evaluates a {@link CohortCrossTabDataSetDefinition} and produces a {@link MapDataSet<Cohort>}
 * 
 * @see CohortDataSetDefinition
 * @see MapDataSet<Cohort>
 */
@Handler(supports={CohortCrossTabDataSetDefinition.class})
public class CohortCrossTabDataSetEvaluator implements DataSetEvaluator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Default Constructor
	 */
	public CohortCrossTabDataSetEvaluator() { }
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) {
		
		if (context == null) {
			context = new EvaluationContext();
		}
		
		MapDataSet data = new MapDataSet(dataSetDefinition, context);
		data.setName(dataSetDefinition.getName());

		CohortCrossTabDataSetDefinition crossTabDef = (CohortCrossTabDataSetDefinition) dataSetDefinition;
		
		DataSetDefinitionService dds = Context.getService(DataSetDefinitionService.class);
		
		MapDataSet rowData = (MapDataSet) dds.evaluate(crossTabDef.getRowCohortDataSetDefinition(), context);
		MapDataSet colData = (MapDataSet) dds.evaluate(crossTabDef.getColumnCohortDataSetDefinition(), context);
		
		for (DataSetColumn rowDataCol : rowData.getDefinition().getColumns()) {
			for (DataSetColumn colDataCol : colData.getDefinition().getColumns()) {
				Cohort rowCohort = (Cohort)rowData.getData().getColumnValue(rowDataCol);
				Cohort colCohort = (Cohort)colData.getData().getColumnValue(colDataCol);
				String key = rowDataCol.getColumnKey() + crossTabDef.getRowColumnDelimiter() + colDataCol.getColumnKey();
				data.addData(key, Cohort.intersect(rowCohort, colCohort));
			}
		}

		return data;
	}
}
