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
package org.openmrs.module.reporting.dataset.column.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.column.EvaluatedColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.JoinColumnDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates join column definitions
 */
@Handler(supports=JoinColumnDefinition.class, order=50)
public class JoinColumnEvaluator implements ColumnEvaluator {

	/** 
	 * @see ColumnEvaluator#evaluate(ColumnDefinition, EvaluationContext)
	 */
	public EvaluatedColumnDefinition evaluate(ColumnDefinition definition, EvaluationContext context) {
		
		JoinColumnDefinition<?> cd = (JoinColumnDefinition<?>) definition;
		DataSetDefinitionService dsds = Context.getService(DataSetDefinitionService.class);
		DataSetQueryService dsqs = Context.getService(DataSetQueryService.class);
		EvaluatedColumnDefinition column = dsds.evaluateColumn(cd.getColumnDefinition(), context);

		// At this point, we have the column values, but these are tied to the ids of the joined base type, so we need to convert
		column = dsqs.convertColumn(column, cd);
		
		return column;
	}

}
