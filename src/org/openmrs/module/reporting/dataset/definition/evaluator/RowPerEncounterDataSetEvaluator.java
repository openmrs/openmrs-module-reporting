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
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.RowPerObjectDataSet;
import org.openmrs.module.reporting.dataset.column.EvaluatedColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;
import org.openmrs.module.reporting.dataset.column.service.DataSetColumnDefinitionService;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.RowPerEncounterDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.AllEncounterQuery;
import org.openmrs.module.reporting.query.encounter.service.EncounterQueryService;

/**
 * The logic that evaluates a {@link RowPerEncounterDataSetDefinition} and produces an {@link DataSet}
 */
@Handler(supports=RowPerEncounterDataSetDefinition.class)
public class RowPerEncounterDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Public constructor
	 */
	public RowPerEncounterDataSetEvaluator() { }
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		
		RowPerEncounterDataSetDefinition dsd = (RowPerEncounterDataSetDefinition) dataSetDefinition;
		DataSetColumnDefinitionService service = Context.getService(DataSetColumnDefinitionService.class);
		context = ObjectUtil.nvl(context, new EvaluationContext());
		
		RowPerObjectDataSet dataSet = new RowPerObjectDataSet(dsd, context);
		
		// Determine the Base Cohort for this DataSet
		if (dsd.getPatientFilter() != null) {
			EvaluatedCohort filterCohort = Context.getService(CohortDefinitionService.class).evaluate(dsd.getPatientFilter(), context);
			context = new EvaluationContext(context);
			if (context.getBaseCohort() == null) {
				context.setBaseCohort(filterCohort);
			}
			else {
				context.setBaseCohort(Cohort.intersect(context.getBaseCohort(), filterCohort));
			}
		}
		
		// Construct an EncounterEvaluationContext based on the encounter filter
		EncounterQueryResult r = null;
		if (dsd.getEncounterFilter() != null) {
			r = Context.getService(EncounterQueryService.class).evaluate(dsd.getEncounterFilter(), context);
		}
		else {
			r = Context.getService(EncounterQueryService.class).evaluate(new AllEncounterQuery(), context);
		}
		EncounterEvaluationContext eec = new EncounterEvaluationContext(context, r);

		// Evaluate each specified ColumnDefinition for all of the included rows and add these to the dataset
		for (Mapped<? extends ColumnDefinition> mappedDef : dsd.getColumnDefinitions()) {
			
			EvaluatedColumnDefinition evaluatedColumnDef = service.evaluate(mappedDef, eec);
			ColumnDefinition cd = evaluatedColumnDef.getDefinition();
			DataSetColumn column = new DataSetColumn(cd.getName(), cd.getName(), cd.getDataType()); // TODO: Support One-Many column definition to column
			
			for (Integer id : r.getMemberIds()) {
				dataSet.addColumnValue(id, column, evaluatedColumnDef.getColumnValues().get(id));
			}
		}

		return dataSet;
	}
}
