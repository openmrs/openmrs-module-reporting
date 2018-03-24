/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
