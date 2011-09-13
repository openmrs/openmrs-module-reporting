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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.RowPerObjectDataSet;
import org.openmrs.module.reporting.dataset.column.EvaluatedColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;
import org.openmrs.module.reporting.dataset.column.service.DataSetColumnDefinitionService;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.RowPerPersonDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.person.PersonQueryResult;
import org.openmrs.module.reporting.query.person.definition.AllPersonQuery;
import org.openmrs.module.reporting.query.person.service.PersonQueryService;

/**
 * The logic that evaluates a {@link RowPerPersonDataSetDefinition} and produces an {@link DataSet}
 */
@Handler(supports=RowPerPersonDataSetDefinition.class)
public class RowPerPersonDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Public constructor
	 */
	public RowPerPersonDataSetEvaluator() { }
	
	/**
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		
		RowPerPersonDataSetDefinition dsd = (RowPerPersonDataSetDefinition) dataSetDefinition;
		DataSetColumnDefinitionService service = Context.getService(DataSetColumnDefinitionService.class);
		context = ObjectUtil.nvl(context, new EvaluationContext());
		
		RowPerObjectDataSet dataSet = new RowPerObjectDataSet(dsd, context);
		
		// Construct an PersonEvaluationContext based on the person filter
		Set<Integer> idsToUse = null;
		if (context instanceof PersonEvaluationContext) {
			PersonQueryResult basePersons = ((PersonEvaluationContext)context).getBasePersons();
			if (basePersons != null) {
				idsToUse = new HashSet<Integer>(basePersons.getMemberIds());
			}
		}
		
		if (dsd.getPersonFilter() != null) {
			PersonQueryResult filterResult = Context.getService(PersonQueryService.class).evaluate(dsd.getPersonFilter(), context);
			if (idsToUse == null) {
				idsToUse = new HashSet<Integer>(filterResult.getMemberIds());
			}
			else {
				idsToUse.retainAll(filterResult.getMemberIds());
			}
		}
		
		if (idsToUse == null) {
			idsToUse = Context.getService(PersonQueryService.class).evaluate(new AllPersonQuery(), context).getMemberIds();
		}
		
		PersonQueryResult r = new PersonQueryResult();
		r.setMemberIds(idsToUse);
		
		PersonEvaluationContext pec = new PersonEvaluationContext(context, r);

		// Evaluate each specified ColumnDefinition for all of the included rows and add these to the dataset
		for (Mapped<? extends ColumnDefinition> mappedDef : dsd.getColumnDefinitions()) {
			
			EvaluatedColumnDefinition evaluatedColumnDef = service.evaluate(mappedDef, pec);
			ColumnDefinition cd = evaluatedColumnDef.getDefinition();
			DataSetColumn column = new DataSetColumn(cd.getName(), cd.getName(), cd.getDataType()); // TODO: Support One-Many column definition to column
			
			for (Integer id : pec.getBasePersons().getMemberIds()) {
				dataSet.addColumnValue(id, column, evaluatedColumnDef.getColumnValues().get(id));
			}
		}

		return dataSet;
	}
}
