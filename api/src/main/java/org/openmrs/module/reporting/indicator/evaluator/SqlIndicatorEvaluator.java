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
package org.openmrs.module.reporting.indicator.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.SimpleIndicatorResult;
import org.openmrs.module.reporting.indicator.SqlIndicator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *  The evaluator that evaluates {@link SqlIndicator} and produces a a {@link SimpleIndicatorResult}.
 */
@Handler(supports={SqlIndicator.class})
public class SqlIndicatorEvaluator implements IndicatorEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());	
	
	@Autowired
	private EvaluationService evaluationService;

	public SimpleIndicatorResult evaluate(Indicator indicator, EvaluationContext context) throws EvaluationException {
		SqlIndicator sqlIndicator = (SqlIndicator) indicator;
		SimpleIndicatorResult result = new SimpleIndicatorResult();
		result.setIndicator(indicator);
		result.setContext(context);
		result.setNumeratorResult(evaluateSql(sqlIndicator.getSql(), context, "sql"));
		result.setDenominatorResult(evaluateSql(sqlIndicator.getDenominatorSql(), context, "denominatorSql"));
		return result;
	}

	protected Number evaluateSql(String sql, EvaluationContext context, String propertyName) throws EvaluationException {
		if (ObjectUtil.isNull(sql)) {
			return null;
		}
		try {
			SqlQueryBuilder qb = new SqlQueryBuilder();
			qb.append(sql);
			qb.setParameters(context.getParameterValues());
			return evaluationService.evaluateToObject(qb, Number.class, context);
		}
		catch (Exception e) {
			throw new EvaluationException(propertyName, e);
		}
	}
}
