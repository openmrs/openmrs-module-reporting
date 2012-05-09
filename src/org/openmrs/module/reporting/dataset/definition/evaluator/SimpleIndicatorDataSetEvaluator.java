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
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimpleIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimpleIndicatorDataSetDefinition.SimpleIndicatorColumn;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.indicator.SimpleIndicatorResult;
import org.openmrs.module.reporting.indicator.service.IndicatorService;

/**
 * The logic that evaluates a {@link SimpleIndicatorDataSetDefinition} and produces an {@link DataSet}
 * @see SimpleIndicatorDataSetDefinition
 */
@Handler(supports = { SimpleIndicatorDataSetDefinition.class })
public class SimpleIndicatorDataSetEvaluator implements DataSetEvaluator{
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	public SimpleIndicatorDataSetEvaluator(){};
	
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		MapDataSet ret = new MapDataSet(dataSetDefinition, context);
		SimpleIndicatorDataSetDefinition dsd = (SimpleIndicatorDataSetDefinition) dataSetDefinition;
		for (DataSetColumn dsc : dsd.getColumns()) {
			ret.getMetaData().addColumn(dsc);
		}
		IndicatorService is = Context.getService(IndicatorService.class);
		
		for (DataSetColumn c : dsd.getColumns()) {
			SimpleIndicatorColumn col = (SimpleIndicatorColumn) c;
			try {
				SimpleIndicatorResult result = (SimpleIndicatorResult) is.evaluate(col.getIndicator(), context);
				ret.addColumnValue(0, c, result.getValue()); // this returns only 1 row
			} catch (Exception ex) {
				throw new EvaluationException("indicator for column " + col.getLabel() + " (" + col.getName() + ")", ex);
			}
		}
		
		return ret;
	}
	
}
