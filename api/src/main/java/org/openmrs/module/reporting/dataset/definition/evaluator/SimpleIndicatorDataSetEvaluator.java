/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
                ret.addData(c, result.getValue());
			}
            catch (Exception ex) {
				throw new EvaluationException("indicator for column " + col.getLabel() + " (" + col.getName() + ")", ex);
			}
		}
		
		return ret;
	}
	
}
