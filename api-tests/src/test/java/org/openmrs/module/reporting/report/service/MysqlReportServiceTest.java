/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.LogicDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Priority;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.Verifies;

@SkipBaseSetup
@Ignore
public class MysqlReportServiceTest extends BaseModuleContextSensitiveTest {

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Test
	@Verifies(value = "should retrieve report requests by definition", method = "getReportRequests(ReportDefinition, Date, Date, Status)")
	public void compareTo_shouldDeleteReportRequestsWhenReportDefintionIsDeleted() throws Exception {
		
		authenticate();
		
		ReportService rs = Context.getService(ReportService.class);
		
		LogicDataSetDefinition dsd = new LogicDataSetDefinition();
		dsd.setName("Gender DSD");
		dsd.addColumn("gender", "Gender", "gender", null);
		dsd = Context.getService(DataSetDefinitionService.class).saveDefinition(dsd);
		
		ReportDefinition rd = new ReportDefinition();
		rd.setName("Test Report");
		rd.addDataSetDefinition("genders", new Mapped<DataSetDefinition>(dsd, null));
		rd = Context.getService(ReportDefinitionService.class).saveDefinition(rd);
		
		RenderingMode mode = new RenderingMode(new CsvReportRenderer(), "CSV", "csv", 100);
		ReportRequest request = new ReportRequest(new Mapped<ReportDefinition>(rd, null), null, mode, Priority.HIGHEST, null);
		rs.runReport(request);
		
		List<ReportRequest> requests = rs.getReportRequests(rd, null, null);
		Assert.assertEquals(1, requests.size());
	}
}