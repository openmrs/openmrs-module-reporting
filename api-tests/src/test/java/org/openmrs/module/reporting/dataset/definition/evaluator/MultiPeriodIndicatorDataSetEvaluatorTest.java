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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;

import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiPeriodIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.MultiPeriodIndicatorDataSetDefinition.Iteration;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class MultiPeriodIndicatorDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {

	/**
	 * @see {@link MultiPeriodIndicatorDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate a MultiPeriodIndicatorDataSetDefinition", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAMultiPeriodIndicatorDataSetDefinition() throws Exception {
		// patient 6's birthdate is 2007-05-27 in the standard test dataset
		DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		Assert.assertEquals(ymd.parse("2007-05-27"), Context.getPatientService().getPatient(6).getBirthdate());
		
		AgeCohortDefinition lessThanOne = new AgeCohortDefinition();
		lessThanOne.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		lessThanOne.setMaxAge(1);
		
		CohortIndicator lessThanOneAtStart = new CohortIndicator();
		lessThanOneAtStart.addParameter(ReportingConstants.START_DATE_PARAMETER);
		lessThanOneAtStart.addParameter(ReportingConstants.END_DATE_PARAMETER);
		lessThanOneAtStart.setUuid(UUID.randomUUID().toString());
		Map<String, Object> mappings = new HashMap<String, Object>();
		mappings.put("effectiveDate", "${startDate}");
		lessThanOneAtStart.setCohortDefinition(lessThanOne, mappings);
		
		Map<String, Object> periodMappings = new HashMap<String, Object>();
		periodMappings.put("startDate", "${startDate}");
		periodMappings.put("endDate", "${endDate}");
		periodMappings.put("location", "${location}");
		
		CohortIndicatorDataSetDefinition def = new CohortIndicatorDataSetDefinition();
		def.addColumn("1", "Indicator", new Mapped<CohortIndicator>(lessThanOneAtStart, periodMappings), "");
		
		MultiPeriodIndicatorDataSetDefinition multi = new MultiPeriodIndicatorDataSetDefinition(def);
		// for every month in 2009, which is the year that patient 6 turns 2 years old.
		Assert.assertEquals(0, Calendar.JANUARY);
		Location loc = new Location(1);
		for (int i = 0; i < 12; ++i) {
			Date startDate = DateUtil.getDateTime(2009, i, 1);
			Date endDate = DateUtil.getEndOfMonth(startDate);
			multi.addIteration(new Iteration(startDate, endDate, loc));
		}
		
		// make sure the number changes from 1 to 0 in June
		DataSet result = Context.getService(DataSetDefinitionService.class).evaluate(multi, null);
		Date june1 = ymd.parse("2009-06-01");
		for (DataSetRow row : result) {
			Date rowStartDate = (Date) row.getColumnValue("startDate");
			if (rowStartDate.compareTo(june1) < 0) {
				Assert.assertEquals("Should be 1 patient before June", 1d, ((CohortIndicatorAndDimensionResult) row.getColumnValue("1")).getValue().doubleValue(), 0);
			} else {
				Assert.assertEquals("Should be 0 patients after June", 0d, ((CohortIndicatorAndDimensionResult) row.getColumnValue("1")).getValue().doubleValue(), 0);
			}
		}
	}
}