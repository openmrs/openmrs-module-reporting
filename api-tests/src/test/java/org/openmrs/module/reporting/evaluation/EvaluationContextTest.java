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
package org.openmrs.module.reporting.evaluation;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.reporting.evaluation.parameter.ParameterException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the EvaluationContext expression parsing
 */
public class EvaluationContextTest extends BaseModuleContextSensitiveTest {
	
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S");
	
	@Test
	public void shouldEvaluateExpression() throws Exception {

		assertEquals(evaluate("${report.d1}"), df.parse("2007-01-10 10:30:17:000"));
		assertEquals(evaluate("${report.d1 - 17s}"), df.parse("2007-01-10 10:30:00:000"));
		assertEquals(evaluate("${report.d1-15d}"), df.parse("2006-12-26 10:30:17:000"));
		assertEquals(evaluate("${report.d1 - 15d}"), df.parse("2006-12-26 10:30:17:000"));
		assertEquals(evaluate("${report.d1- 15d}"), df.parse("2006-12-26 10:30:17:000"));
		assertEquals(evaluate("${report.d1 -15d}"), df.parse("2006-12-26 10:30:17:000"));
		assertEquals(evaluate("${report.d1+3w}"), df.parse("2007-01-31 10:30:17:000"));
		assertEquals(evaluate("${report.d1-12m}"), df.parse("2006-01-10 10:30:17:000"));
		assertEquals(evaluate("${report.d1-1y}"), df.parse("2006-01-10 10:30:17:000"));
		assertEquals(evaluate("${report.d1+37d}"), df.parse("2007-02-16 10:30:17:000"));
		assertEquals(evaluate("${report.d1-10w}"), df.parse("2006-11-01 10:30:17:000"));
		assertEquals(evaluate("${report.d1+3h}"), df.parse("2007-01-10 13:30:17:000"));
		assertEquals(evaluate("${report.d1+36h}"), df.parse("2007-01-11 22:30:17:000"));
		assertEquals(evaluate("${report.d1-1m-1w}"), df.parse("2006-12-3 10:30:17:000"));
		assertEquals(evaluate("${report.d1+36m-10w+24h}"), df.parse("2009-11-02 10:30:17:000"));
		assertEquals(evaluate("${report.d1 + 36m - 10w + 24h}"), df.parse("2009-11-02 10:30:17:000"));
        assertEquals(evaluate("${report.d1 + 1m - 1ms}"), df.parse("2007-02-10 10:30:16:999"));
        assertEquals(evaluate("${report.testInt + 1}"), 8);
        assertEquals(evaluate("${report.testInt - 3}"), 4);
        assertEquals(evaluate("${report.testInt * 2}"), 14);
        assertEquals(evaluate("${report.testInt / 3}"), 7/3);
        assertEquals(evaluate("${report.testInt +1 *2}"), 16);
        assertEquals(evaluate("${report.testDouble + 1}"), 6.0);
        assertEquals(evaluate("${report.testDouble + 2.5}"), 7.5);
        assertEquals(evaluate("${report.testDouble -0.1}"), 4.9);
        assertEquals(evaluate("${report.testDouble*2}"), 10.0);
        assertEquals(evaluate("${report.testDouble*2.5}"), 12.5);
        assertEquals(evaluate("${report.testDouble / 2.5}"), 2.0);
		assertEquals(evaluate("${report.testDouble}"), new Double(5));
		assertEquals(evaluate("${report.testDouble|0}"), "5");
		assertEquals(evaluate("${report.testDouble|3}"), "5.000");
		assertEquals(evaluate("${report.gender}"), "male");
		assertEquals(evaluate("report.gender"), "report.gender");
		assertEquals(evaluate("hello ${report.gender} person"), "hello male person");
		assertEquals(evaluate("From ${report.d1|yyyy-MM-dd} to ${report.d1+3w|yyyy-MM-dd} for ${report.gender}s"), 
							   "From 2007-01-10 to 2007-01-31 for males");
	}

    @Test
    public void evaluateParameterExpression_shouldFailForBadExpressions() throws Exception {
        String[] badExpressions = new String[]{
                "report.testInt - 1y",
                "report.testDouble + 2d",
                "report.testInt + 1.5.2",
                "report.d1 + 1.5h",
                "report.d1 + 7x",
                "report.d1 / 2",
                "report.d1 * 3",
                "report.testInt + x",
                "report.testInt + report.testDouble"
        };
        for (String badExpression : badExpressions) {
            try {
                Object actual = evaluate("${" + badExpression + "}");
                if (!actual.equals(badExpression)) {
                    Assert.fail("Expression should have failed: " + badExpression + " => " + actual);
                }
            } catch (ParameterException ex) {
                // expected
            }
        }
    }

	@Test
	public void shouldEvaluatePredefinedParameters() throws Exception {
		
		EvaluationContext context = new EvaluationContext(df.parse("2007-01-17 10:30:17:123"));

		assertEquals(evaluate("${now}", context), context.getEvaluationDate());
		assertEquals(evaluate("${start_of_today}", context), df.parse("2007-01-17 00:00:00:000"));
		assertEquals(evaluate("${end_of_today}", context), df.parse("2007-01-17 23:59:59:999"));
		assertEquals(evaluate("${start_of_last_month}", context), df.parse("2006-12-01 00:00:00:000"));
		assertEquals(evaluate("${end_of_last_month}", context), df.parse("2006-12-31 23:59:59:999"));
	}

	@Test
	public void shouldParseParameterNameFromExpression() throws Exception {
		assertEquals("startDate", parseParameter("${startDate}"));
		assertEquals("report.d1", parseParameter("${report.d1 - 17s}"));
		assertEquals("endDate", parseParameter("${endDate-15d}"));
		assertEquals("reportDate", parseParameter("${reportDate - 15d}"));
		assertEquals("reportDate", parseParameter("${reportDate- 15d}"));
		assertEquals("reportDate", parseParameter("${reportDate -1ms}"));
		assertEquals("reportDate.d1", parseParameter("${reportDate.d1-1m-1w}"));
		assertEquals("reportDate", parseParameter("reportDate"));
		assertEquals("startDate", parseParameter("startDate"));
	}
	
	/**
	 * Helper method to evaluate an expression
	 * @param context
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public Object evaluate(String expression, EvaluationContext context) throws Exception {
		context.addParameterValue("report.d1", df.parse("2007-01-10 10:30:17:000"));
		context.addParameterValue("report.gender", "male");
		context.addParameterValue("report.testDouble", new Double(5));
		context.addParameterValue("report.testInt", 7);
		return EvaluationUtil.evaluateExpression(expression, context);
	}
	
	/**
	 * Helper method to evaluate an expression
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public Object evaluate(String expression) throws Exception {
		return evaluate(expression, new EvaluationContext());
	}

	public String parseParameter(String expression) {
		return EvaluationUtil.parseParameterNameFromExpression(expression);
	}
}
