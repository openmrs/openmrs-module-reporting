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

import java.util.Map;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.column.EvaluatedColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.PropertyColumnDefinition;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates properties of a particular type
 */
@Handler(supports=PropertyColumnDefinition.class, order=50)
public class PropertyColumnEvaluator implements ColumnEvaluator {

	/** 
	 * @see ColumnEvaluator#evaluate(ColumnDefinition, EvaluationContext)
	 */
	public EvaluatedColumnDefinition evaluate(ColumnDefinition definition, EvaluationContext context) {
		PropertyColumnDefinition pcd = (PropertyColumnDefinition) definition;
		EvaluatedColumnDefinition c = new EvaluatedColumnDefinition(definition, context);
		DataSetQueryService queryService = Context.getService(DataSetQueryService.class);
		
		Map<Integer, Object> vals = queryService.getPropertyValues(pcd.getBaseType(), pcd.getProperty(), context);
		for (Map.Entry<Integer, Object> e : vals.entrySet()) {
			Object value = e.getValue();
			value = pcd.getPropertyConverter() == null ? value : pcd.getPropertyConverter().convert(value);
			value = pcd.getConverter() == null ? value : pcd.getConverter().convert(value);
			c.addColumnValue(e.getKey(), value);
		}
		return c;
	}
}
