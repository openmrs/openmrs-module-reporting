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
package org.openmrs.module.evaluation;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

/**
 *
 */
public class EvaluationContextTest {
	
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S");
	
	@Test
	public void shouldEvaluateExpression() throws Exception {

		assertEquals(evaluate("${report.d1}", Date.class), df.parse("2007-01-10 10:30:17:000"));
		assertEquals(evaluate("${report.d1-15d}", Date.class), df.parse("2006-12-26 10:30:17:000"));
		assertEquals(evaluate("${report.d1+3w}", Date.class), df.parse("2007-01-31 10:30:17:000"));
		assertEquals(evaluate("${report.d1-12m}", Date.class), df.parse("2006-01-10 10:30:17:000"));
		assertEquals(evaluate("${report.d1-1y}", Date.class), df.parse("2006-01-10 10:30:17:000"));
		assertEquals(evaluate("${report.d1+37d}", Date.class), df.parse("2007-02-16 10:30:17:000"));
		assertEquals(evaluate("${report.d1-10w}", Date.class), df.parse("2006-11-01 10:30:17:000"));
		assertEquals(evaluate("${report.gender}", String.class), "male");
		assertEquals(evaluate("report.gender", String.class), "report.gender");
		assertEquals(evaluate("hello ${report.gender} person", String.class), "hello male person");
		assertEquals(evaluate("From ${report.d1|yyyy-MM-dd} to ${report.d1+3w|yyyy-MM-dd} for ${report.gender}s", String.class), 
							   "From 2007-01-10 to 2007-01-31 for males");
	}
	
	@Test
	public void shouldEvaluatePredefinedParameters() throws Exception {
		
		EvaluationContext context = new EvaluationContext(df.parse("2007-01-17 10:30:17:123"));

		assertEquals(evaluate("${now}", Date.class, context), context.getEvaluationDate());
		assertEquals(evaluate("${start_of_today}", Date.class, context), df.parse("2007-01-17 00:00:00:000"));
		assertEquals(evaluate("${end_of_today}", Date.class, context), df.parse("2007-01-17 23:59:59:999"));
		assertEquals(evaluate("${start_of_last_month}", Date.class, context), df.parse("2006-12-01 00:00:00:000"));
		assertEquals(evaluate("${end_of_last_month}", Date.class, context), df.parse("2006-12-31 23:59:59:999"));
	}
	
	/**
	 * Helper method to evaluate an expression
	 * @param context
	 * @param expression
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public Object evaluate(String expression, Class<?> type, EvaluationContext context) throws Exception {
		context.addParameterValue("report.d1", df.parse("2007-01-10 10:30:17:000"));
		context.addParameterValue("report.gender", "male");
		return EvaluationUtil.evaluateExpression(expression, context, type);
	}
	
	/**
	 * Helper method to evaluate an expression
	 * @param expression
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public Object evaluate(String expression, Class<?> type) throws Exception {
		return evaluate(expression, type, new EvaluationContext());
	}
	
}
