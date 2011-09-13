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
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.RowPerObjectDataSet;
import org.openmrs.module.reporting.dataset.column.EvaluatedColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;
import org.openmrs.module.reporting.dataset.column.service.DataSetColumnDefinitionService;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.RowPerPatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * The logic that evaluates a {@link RowPerPatientDataSetDefinition} and produces an {@link DataSet}
 */
@Handler(supports=RowPerPatientDataSetDefinition.class)
public class RowPerPatientDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Public constructor
	 */
	public RowPerPatientDataSetEvaluator() { }
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		
		RowPerPatientDataSetDefinition dsd = (RowPerPatientDataSetDefinition) dataSetDefinition;
		DataSetColumnDefinitionService service = Context.getService(DataSetColumnDefinitionService.class);
		context = ObjectUtil.nvl(context, new EvaluationContext());
		
		RowPerObjectDataSet dataSet = new RowPerObjectDataSet(dsd, context);

		if (dsd.getPatientFilter() != null) {
			context = new EvaluationContext(context);
			EvaluatedCohort filterCohort = Context.getService(CohortDefinitionService.class).evaluate(dsd.getPatientFilter(), context);
			if (context.getBaseCohort() == null) {
				context.setBaseCohort(filterCohort);
			}
			else {
				context.setBaseCohort(Cohort.intersect(context.getBaseCohort(), filterCohort));
			}
		}
		Cohort c = context.getBaseCohort();
		if (c == null) {
			AllPatientsCohortDefinition allPatients = new AllPatientsCohortDefinition();
			c = Context.getService(CohortDefinitionService.class).evaluate(allPatients, context);
		}

		// Evaluate each specified ColumnDefinition for all of the included rows and add these to the dataset
		for (Mapped<? extends ColumnDefinition> mappedDef : dsd.getColumnDefinitions()) {
			
			EvaluatedColumnDefinition evaluatedColumnDef = service.evaluate(mappedDef, context);
			ColumnDefinition cd = evaluatedColumnDef.getDefinition();
			DataSetColumn column = new DataSetColumn(cd.getName(), cd.getName(), cd.getDataType()); // TODO: Support One-Many column definition to column
			
			for (Integer id : c.getMemberIds()) {
				dataSet.addColumnValue(id, column, evaluatedColumnDef.getColumnValues().get(id));
			}
		}

		return dataSet;
	}
}
