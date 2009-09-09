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
package org.openmrs.module.indicator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.cohort.definition.LocationCohortDefinition;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Mapped;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.indicator.aggregation.CountAggregator;
import org.openmrs.module.indicator.dimension.CohortDefinitionDimension;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.renderer.CsvReportRenderer;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.util.ParameterizableUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 *
 */
public class IndicatorTest extends BaseModuleContextSensitiveTest {
	
	/* Logger */
	private static Log log = LogFactory.getLog(IndicatorTest.class);	
	
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Before
	public void runBeforeAllTests() throws Exception {
		//executeDataSet("org/openmrs/module/indicator/include/IndicatorTest.xml");
		authenticate();
	}
	
	
	@Test
	public void evaluateIndicator() throws Exception { 
		
		String uuid = "79e204e8-0360-4058-9966-072f371b5e6c"; 
		Indicator indicator = Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);
		log.info("indicator = " + indicator);
		
		//Map<String, Object> parameterValues = new HashMap<String, Object>();
		//parameterValues.put("startDate", new Date());
		//parameterValues.put("endDate", new Date());
		//parameterValues.put("location", new Location());
		
		
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("startDate", new Date());
		context.addParameterValue("endDate", new Date());
		context.addParameterValue("location", new Date());
		
		
		//context.setParameterValues(parameterValues);
		
		IndicatorResult result = 
			Context.getService(IndicatorService.class).evaluate(indicator, context);		
	
		log.info("Result: " + result);
	}
	
}
