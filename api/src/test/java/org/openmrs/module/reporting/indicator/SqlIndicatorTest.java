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
package org.openmrs.module.reporting.indicator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.reporting.common.Fraction;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;

import java.math.BigDecimal;

/**
 * Test class for testing evaluation of SQLIndicators
 */
public class SqlIndicatorTest extends BaseModuleContextSensitiveTest {

	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/" + new TestUtil().getTestDatasetFilename("ReportTestDataset"));
	}

	@Autowired
	IndicatorService indicatorService;
	
	@Test
	public void sqlIndicator_shouldEvaluateSqlIndicator() throws Exception {
		assertIndicatorValue("SELECT distinct(251) as res from patient", 251);
		assertIndicatorValue("SELECT distinct(0.7154) as res from patient", 0.7154);
	}

	@Test
	public void sqlIndicator_shouldEvaluateSqlIndicatorDivideByZero() throws Exception {
		SqlIndicator indicator = new SqlIndicator();
		indicator.setSql("SELECT distinct(4736) as res from patient");
		indicator.setDenominatorSql("SELECT distinct(0) as res2 from patient");
		assertIndicatorValue(indicator, new Fraction(4736, 0));
	}

	@Test
	public void sqlIndicator_shouldEvaluateSqlIndicatorNullNumerator() throws Exception {
		assertIndicatorValue("SELECT distinct(null) as res from patient", Double.NaN);
	}

	@Test
	public void sqlIndicator_shouldEvaluateSqlIndicatorNullDenominator() throws Exception {
		assertIndicatorValue("SELECT distinct(55) as res from patient", "SELECT distinct(null) as res from patient", 55);
	}

	@Test
	public void sqlIndicator_shouldEvaluateSqlIndicatorUsesParameters() throws Exception {
		SqlIndicator indicator = new SqlIndicator();
		indicator.addParameter(new Parameter("numValue", "numValue", Integer.class));
		indicator.addParameter(new Parameter("denValue", "denValue", Integer.class));
		indicator.setSql("SELECT patient_id from patient where patient_id = :numValue");
		indicator.setDenominatorSql("SELECT patient_id from patient where patient_id = :denValue");

		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("numValue", 6);
		context.addParameterValue("denValue", 24);

		assertIndicatorValue(indicator, new Fraction(6, 24), context);
	}

	@Test
	@ExpectedException(RuntimeException.class)
	public void sqlIndicator_shouldEvaluateSqlIndicatorDecimals() throws Exception {
		assertIndicatorValue("SELECT distinct(.222) as res from patient", "SELECT distinct(.44) as res2 from patient", null);
	}
	
	@Test
	@ExpectedException(EvaluationException.class)
	public void sqlIndicator_shouldNotAllowQueriesThatReturnMoreThanOneColumn() throws Exception {
		assertIndicatorValue("SELECT distinct(.222) as res, 33 as res2 from patient", null);
	}
	
	@Test
	@ExpectedException(EvaluationException.class)
	public void sqlIndicator_shouldNotAllowQueriesThatReturnMoreThanOneRow() throws Exception {
		assertIndicatorValue("SELECT person_id from person", null);
	}

	protected void assertIndicatorValue(SqlIndicator indicator, Number expectedValue, EvaluationContext context) throws Exception {
		SimpleIndicatorResult r = (SimpleIndicatorResult)indicatorService.evaluate(indicator, context);
		Number result = r.getValue();
		if (result instanceof BigDecimal) {
			result = result.doubleValue();
		}
		Assert.assertEquals(expectedValue, result);
	}

	protected void assertIndicatorValue(SqlIndicator indicator, Number expectedValue) throws Exception {
		assertIndicatorValue(indicator, expectedValue, new EvaluationContext());
	}

	protected void assertIndicatorValue(String numeratorSql, Number expectedValue) throws Exception {
		assertIndicatorValue(numeratorSql, null, expectedValue);
	}

	protected void assertIndicatorValue(String numeratorSql, String denominatorSql, Number expectedValue) throws Exception {
		SqlIndicator indicator = new SqlIndicator();
		indicator.setSql(numeratorSql);
		indicator.setDenominatorSql(denominatorSql);
		assertIndicatorValue(indicator, expectedValue);
	}
}
