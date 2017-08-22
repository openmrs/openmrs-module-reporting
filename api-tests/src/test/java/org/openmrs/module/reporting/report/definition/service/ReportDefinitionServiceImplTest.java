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
package org.openmrs.module.reporting.report.definition.service;


import org.junit.Test;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ReportDefinitionServiceImplTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private ReportDefinitionService reportDefinitionService;
	
	@Autowired
	private ReportService reportService;
	
	/**
	 * @see ReportDefinitionServiceImpl#purgeDefinition(ReportDefinition)
	 * @verifies purge report designs
	 */
	@Test
	public void purgeDefinition_shouldPurgeReportDesigns() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportDefinitionServiceImplTest.xml");
    	
    	ReportDefinition definition = reportDefinitionService.getDefinition(80);
    	reportDefinitionService.purgeDefinition(definition);
	    
		assertThat(reportService.getReportDesign(3), nullValue());
		assertThat(reportService.getReportDesign(4), nullValue());
	}

	/**
	 * @see ReportDefinitionServiceImpl#purgeDefinition(String)
	 * @verifies purge report definition by uuid and associated report designs and requests
	 */
	@Test
	public void purgeDefinition_shouldPurgeDesignsAndRequestsAndDefinition() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportDefinitionServiceImplTest.xml");

		reportDefinitionService.purgeDefinition("c11f5354-9567-4cc5-b3ef-163e28873926");

		assertNull(reportService.getReportDesign(3));
		assertNull(reportService.getReportDesign(4));
		assertNull(reportService.getReportRequest(1));
		assertNull(reportService.getReportRequest(3));
		assertNull(reportDefinitionService.getDefinition(80));
	}
}