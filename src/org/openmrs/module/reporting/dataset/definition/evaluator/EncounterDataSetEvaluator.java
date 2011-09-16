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
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.service.EncounterDataService;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.RowPerObjectDataSet;
import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.SingleColumnDefinition;
import org.openmrs.module.reporting.dataset.column.service.DataSetColumnDefinitionService;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.QueryUtil;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.module.reporting.query.encounter.definition.AllEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;

/**
 * The logic that evaluates a {@link EncounterDataSetDefinition} and produces an {@link DataSet}
 */
@Handler(supports=EncounterDataSetDefinition.class)
public class EncounterDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Public constructor
	 */
	public EncounterDataSetEvaluator() { }
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		
		EncounterDataSetDefinition dsd = (EncounterDataSetDefinition) dataSetDefinition;
		DataSetColumnDefinitionService service = Context.getService(DataSetColumnDefinitionService.class);
		context = ObjectUtil.nvl(context, new EvaluationContext());
		
		RowPerObjectDataSet dataSet = new RowPerObjectDataSet(dsd, context);
		
		// Construct an EncounterEvaluationContext based on the encounter filter
		EncounterIdSet r = null;
		if (dsd.getRowFilters() != null) {
			for (Mapped<? extends EncounterQuery> q : dsd.getRowFilters()) {
				EncounterIdSet s = Context.getService(EncounterQueryService.class).evaluate(q, context);
				r = QueryUtil.intersectNonNull(r, s);
			}
		}
		if (r == null) {
			r = Context.getService(EncounterQueryService.class).evaluate(new AllEncounterQuery(), context);
		}
		EncounterEvaluationContext eec = new EncounterEvaluationContext(context, r);

		// Evaluate each specified ColumnDefinition for all of the included rows and add these to the dataset
		for (ColumnDefinition cd : dsd.getColumnDefinitions()) {
			
			Mapped<? extends EncounterDataDefinition> dataDef = (Mapped<? extends EncounterDataDefinition>) ((SingleColumnDefinition)cd).getDataDefinition();
			EvaluatedEncounterData data = Context.getService(EncounterDataService.class).evaluate(dataDef, eec);
			
			DataSetColumn column = new DataSetColumn(cd.getName(), cd.getName(), dataDef.getParameterizable().getDataType()); // TODO: Support One-Many column definition to column
			
			for (Integer id : r.getMemberIds()) {
				dataSet.addColumnValue(id, column, data.getData().get(id));
			}
		}

		return dataSet;
	}
}
