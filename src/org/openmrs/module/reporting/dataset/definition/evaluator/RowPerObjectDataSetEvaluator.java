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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.RowPerObjectDataSet;
import org.openmrs.module.reporting.dataset.column.EvaluatedColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.RowPerEncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.RowPerObjectDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * The logic that evaluates a {@link RowPerObjectDataSetDefinition} and produces an {@link DataSet}
 * @see RowPerEncounterDataSetDefinition
 */
@Handler(supports={RowPerObjectDataSetDefinition.class})
public class RowPerObjectDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Public constructor
	 */
	public RowPerObjectDataSetEvaluator() { }
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	@SuppressWarnings("unchecked")
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) {
		
		RowPerObjectDataSetDefinition<? extends ColumnDefinition> dsd = (RowPerObjectDataSetDefinition<? extends ColumnDefinition>) dataSetDefinition;
		RowPerObjectDataSet dataSet = new RowPerObjectDataSet(dsd, context);
		DataSetDefinitionService dsds = Context.getService(DataSetDefinitionService.class);
		
		Cohort baseCohort = context.getBaseCohort();
		if (baseCohort == null) {
			baseCohort = Context.getPatientSetService().getAllPatients();
		}
		
		// TODO: We probably want to add a way to filter down more thoroughly between column evaluations
		// For example, keep contextual references to the encounterIds, obsIds, patientIds, etc that are still
		// valid, and pass those through to the ColumnEvaluators as appropriate?

		int columnNum = 0;
		for (Mapped<? extends ColumnDefinition> mappedDef : dsd.getColumnDefinitions()) {
			columnNum++;
			EvaluatedColumnDefinition evaluatedColumnDef = dsds.evaluateColumn(mappedDef, context);
			ColumnDefinition cd = evaluatedColumnDef.getDefinition();
			
			if (columnNum > 1) {
				evaluatedColumnDef.retainColumnValues(dataSet.getRows().keySet());
			}
			
	    	for (Map.Entry<Integer, Object> e : evaluatedColumnDef.getColumnValues().entrySet()) {
	    		Integer id = e.getKey();
	    		Object value = e.getValue();
	    		if (columnNum == 1 || dataSet.getRows().containsKey(id)) {
	    			DataSetColumn column = new DataSetColumn(cd.getName(), cd.getName(), cd.getDataType());
	    			dataSet.addColumnValue(id, column, value);
	    		}
	    	}
		}

		return dataSet;
	}
}
