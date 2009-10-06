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
package org.openmrs.module.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.DataSet;
import org.openmrs.module.dataset.DataSetRow;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.module.tracnet.util.TracNetReportUtil;
import org.openmrs.module.util.DateUtil;
import org.openmrs.test.BaseContextSensitiveTest;

/**
 * Test class that tries to run a portion of the Pepfar monthly report
 */
public class TracNetReportTest extends BaseContextSensitiveTest {
	
	Log log = LogFactory.getLog(getClass());
	
	DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
	
	@Before 
	public void beforeTest() throws Exception { 		
		authenticate();		
	}
	
	@Test
	public void shouldGenerateTracNetReport() throws Exception { 
		//initializeInMemoryDatabase();
		//executeDataSet("org/openmrs/report/include/TracNetReportTest.xml");
				
		// Setup parameters to pass to the evaluation context
		Calendar calendar = Calendar.getInstance();		
		calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		calendar.set(Calendar.YEAR, 2009);		
		Date startDate = DateUtil.getStartOfMonth(calendar.getTime());
		Date endDate = DateUtil.getEndOfMonth(calendar.getTime());				
		Location rwinkwavu = Context.getLocationService().getLocation(26);

		// Create and populate the evaluation context
		EvaluationContext evalContext = new EvaluationContext();
		evalContext.addParameterValue("startDate", startDate);
		evalContext.addParameterValue("endDate", endDate);
		evalContext.addParameterValue("location", rwinkwavu);

		
		// Build the report definition 
		ReportDefinition reportDefinition = TracNetReportUtil.getTracNetReportDefinition();
		
		// Evaluate the report definition and display dataset
		ReportData reportData = Context.getService(ReportService.class).evaluate(reportDefinition, evalContext);
		for (DataSet<?> dataSet : reportData.getDataSets().values()) { 
			for(DataSetRow<?> row : dataSet) { 
				for(DataSetColumn column : row.getColumnValues().keySet()) { 
					
					CohortIndicatorAndDimensionResult result = 
						(CohortIndicatorAndDimensionResult) row.getColumnValue(column);
					
					log.info(column.getColumnKey() + " " + column.getDisplayName() + " " + result.getValue());
				}
			}			
		}			
	}
}
