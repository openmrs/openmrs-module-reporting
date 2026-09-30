/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.definition.service;


import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;


import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
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
		assertThat(reportService.getReportDesign(3), notNullValue());
		assertThat(reportService.getReportDesign(4), notNullValue());
		Context.clearSession();

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
		assertNotNull(reportService.getReportDesignByUuid("d7a82b63-1066-4c1d-9b43-b405851fc467"));
		assertNotNull(reportService.getReportDesignByUuid("e7a82b63-1066-4c1d-9b43-b405851fc467"));
		assertNotNull(reportService.getReportRequestByUuid("h8a82b63-1066-4c1d-9b43-b405851fc467"));
		assertNotNull(reportService.getReportRequestByUuid("b0a82b63-1066-4c1d-9b43-b405851fc467"));
		Context.clearSession();

		reportDefinitionService.purgeDefinition("c11f5354-9567-4cc5-b3ef-163e28873926");

		assertNull(reportService.getReportDesignByUuid("d7a82b63-1066-4c1d-9b43-b405851fc467"));
		assertNull(reportService.getReportDesignByUuid("e7a82b63-1066-4c1d-9b43-b405851fc467"));
		assertNull(reportService.getReportRequestByUuid("h8a82b63-1066-4c1d-9b43-b405851fc467"));
		assertNull(reportService.getReportRequestByUuid("b0a82b63-1066-4c1d-9b43-b405851fc467"));
		assertNull(reportDefinitionService.getDefinitionByUuid("c11f5354-9567-4cc5-b3ef-163e28873926"));
	}
}