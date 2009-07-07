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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Testing the cohort definition persister.  
 */
public class ReportServiceTest extends BaseModuleContextSensitiveTest {

	// Logger
	private Log log = LogFactory.getLog(this.getClass());	
	
	@Before
	public void runBeforeTest() throws Exception { 
		authenticate();		
	}
	
	@Test
	public void shouldSaveReportSchema() throws Exception { 		
		ReportService service = Context.getService(ReportService.class);
		ReportSchema reportSchema = new ReportSchema();		
		reportSchema.setName("Testing");
		ReportSchema savedReportSchema = service.saveReportSchema(reportSchema);		
		Assert.assertTrue(savedReportSchema.getId()!=null);
	}
	



}