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


import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class ReportDefinitionServiceImplTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	ReportDefinitionService reportDefinitionService;
	
	@Autowired
	ReportService reportService;
	
	/**
	 * @see ReportDefinitionServiceImpl#purgeDefinition(ReportDefinition)
	 * @verifies purge report designs
	 */
	@Test
	public void purgeDefinition_shouldPurgeReportDesigns() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportDefinitionServiceImplTest.xml");
    	
    	ReportDefinition definition = reportDefinitionService.getDefinition(80);
    	reportDefinitionService.purgeDefinition(definition);
	    
    	ReportDesign reportDesign = reportService.getReportDesign(2);
    	assertThat(reportDesign, nullValue());
	}
}