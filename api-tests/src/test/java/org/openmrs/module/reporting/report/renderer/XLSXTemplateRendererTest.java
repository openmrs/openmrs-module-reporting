/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.renderer;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsClassLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Supports rendering a report to Excel, crated based on {@link ExcelTemplateRendererTest}
 */
public class XLSXTemplateRendererTest extends BaseModuleContextSensitiveTest {

	private static final String REPORT_NAME = "Test XLSX Report";
	private static final String DATA_SET_NAME = "dataSet";
	private static final String SQL_QUERY = "select p.date_created,\n" +
			"    n.gender,\n" +
			"    n.birthdate\n" +
			"from patient p, person n where p.patient_id = n.person_id";
	private static final String DESIGN_NAME = "XLSXDesign";
	private static final String PROP_NAME = "repeatingSections";
	private static final String PROP_VALUE = "sheet:1,row:2,dataset:dataSet";
	private static final String RESOURCE_NAME = "template.xls";
	private static final String TEMPLATE_FILE = "org/openmrs/module/reporting/report/renderer/XLSXTemplate.xlsx";
	private static final String OUTPUT_FILE_NAME = "XLSXTemplateRendererTest.xls";

	@Test
	public void shouldRenderToExcelTemplate() throws Exception {
		ReportDefinition report = new ReportDefinition();
		report.setName(REPORT_NAME);
		
		SqlDataSetDefinition dataSet = new SqlDataSetDefinition();
		dataSet.setName(DATA_SET_NAME);
		dataSet.setSqlQuery(SQL_QUERY);
		report.addDataSetDefinition(DATA_SET_NAME, dataSet, null);

		final ReportDesign design = new ReportDesign();
		design.setName(DESIGN_NAME);
		design.setReportDefinition(report);
		design.setRendererType(ExcelTemplateRenderer.class);
		
		Properties props = new Properties();
		props.put(PROP_NAME, PROP_VALUE);

		design.setProperties(props);
		
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName(RESOURCE_NAME);
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream(TEMPLATE_FILE);
		resource.setContents(IOUtils.toByteArray(is));
		IOUtils.closeQuietly(is);
		design.addResource(resource);

		// For now, we need this little magic to simulate what would happen if this were all stored in the database via the UI
		ExcelTemplateRenderer renderer = new ExcelTemplateRenderer() {
			public ReportDesign getDesign(String argument) {
				return design;
			}
		};
		// We construct an EvaluationContext (in this case the parameters aren't used, but included here for reference)
		
		EvaluationContext context = new EvaluationContext();
		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		ReportData data = rs.evaluate(report, context);

		String outFile = System.getProperty("java.io.tmpdir") + File.separator + OUTPUT_FILE_NAME;
		FileOutputStream fos = new FileOutputStream(outFile);
		try {
			renderer.render(data, "", fos);
		} finally {
			fos.close();
		}
	}
}
