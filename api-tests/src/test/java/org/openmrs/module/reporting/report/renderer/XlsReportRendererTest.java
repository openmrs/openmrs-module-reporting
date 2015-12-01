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
package org.openmrs.module.reporting.report.renderer;

import org.junit.Assert;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.junit.Test;
import org.openmrs.module.reporting.common.ExcelUtil;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Verify that XlsReportRenderer outputs as expected
 */
public class XlsReportRendererTest extends BaseModuleContextSensitiveTest {

    @Autowired
    ReportDefinitionService reportDefinitionService;

    @Test
    public void testXlsReportRenderingWithoutHeaders() throws Exception {
		Workbook wb = renderToXls(false);

		Assert.assertEquals(3, wb.getNumberOfSheets());
		Assert.assertNotNull(wb.getSheet("males"));
		Assert.assertNotNull(wb.getSheet("females"));
		Assert.assertNotNull(wb.getSheet("encounters"));

		testValue(wb, "males", 1, 1, "patient_id");
		testValue(wb, "males", 1, 2, "gender");
		testValue(wb, "males", 1, 3, "birthdate");
    }

	@Test
	public void testXlsReportRenderingWithHeaders() throws Exception {
		Workbook wb = renderToXls(true);
		Assert.assertEquals(3, wb.getNumberOfSheets());
		testValue(wb, "males", 1, 1, "Gender Data Set");
		testValue(wb, "females", 1, 1, "Gender Data Set");
		testValue(wb, "encounters", 1, 1, "encounters");
		testValue(wb, "males", 2, 1, "Gender:");
		testValue(wb, "males", 2, 2, "M");
		testValue(wb, "females", 2, 1, "Gender:");
		testValue(wb, "females", 2, 2, "F");
		testValue(wb, "encounters", 2, 1, "");
		testValue(wb, "encounters", 2, 1, "");
	}

	public void testValue(Workbook wb, String sheetName, int rowNum, int colNum, String value) {
		Sheet sheet = wb.getSheet(sheetName);
		Row row = CellUtil.getRow(rowNum-1, sheet);
		Cell cell = CellUtil.getCell(row, colNum-1);
		Assert.assertEquals(value.toLowerCase(), cell.getStringCellValue().toLowerCase());
	}

	protected ReportDefinition getReportDefinition() {
		ReportDefinition rd = new ReportDefinition();
		rd.setName("Testing");

		SqlDataSetDefinition namedDataSet = new SqlDataSetDefinition();
		namedDataSet.setName("Gender Data Set");
		namedDataSet.addParameter(new Parameter("gender", "Gender", String.class));
		namedDataSet.setSqlQuery("select p.patient_id, n.gender, n.birthdate from patient p, person n where p.patient_id = n.person_id and n.gender = :gender");

		rd.addDataSetDefinition("males", Mapped.map(namedDataSet, "gender=M"));
		rd.addDataSetDefinition("females", Mapped.map(namedDataSet, "gender=F"));

		SqlDataSetDefinition unnamedDataSet = new SqlDataSetDefinition();
		unnamedDataSet.setSqlQuery("select encounter_id, patient_id, encounter_datetime from encounter");
		rd.addDataSetDefinition("encounters", unnamedDataSet, null);
		return rd;
	}

	protected Workbook renderToXls(boolean includeHeaders) throws Exception {

		ReportDefinition rd = getReportDefinition();
		ReportData data = reportDefinitionService.evaluate(rd, new EvaluationContext());

		final ReportDesign design = new ReportDesign();
		design.setName("TestDesign");
		design.setReportDefinition(rd);
		design.setRendererType(XlsReportRenderer.class);
		Properties props = new Properties();
		props.setProperty(XlsReportRenderer.INCLUDE_DATASET_NAME_AND_PARAMETERS_PROPERTY, Boolean.toString(includeHeaders));
		design.setProperties(props);

		XlsReportRenderer renderer = new XlsReportRenderer() {
			public ReportDesign getDesign(String argument) {
				return design;
			}
		};

		String outFile = System.getProperty("java.io.tmpdir") + File.separator + "xlsReportRendererTest"+includeHeaders+".xls";
		FileOutputStream fos = new FileOutputStream(outFile);
		renderer.render(data, "xxx:xls", fos);
		fos.close();

		return ExcelUtil.loadWorkbookFromFile(outFile);
	}

    @Test
    public void renderToXlsWithPassword() throws Exception {

        ReportDefinition rd = getReportDefinition();
        ReportData data = reportDefinitionService.evaluate(rd, new EvaluationContext());

        final ReportDesign design = new ReportDesign();
        design.setName("TestDesign");
        design.setReportDefinition(rd);
        design.setRendererType(XlsReportRenderer.class);
        Properties props = new Properties();
        props.setProperty(XlsReportRenderer.PASSWORD_PROPERTY, "foobar");
        design.setProperties(props);

        XlsReportRenderer renderer = new XlsReportRenderer() {
            public ReportDesign getDesign(String argument) {
                return design;
            }
        };

        String outFile = System.getProperty("java.io.tmpdir") + File.separator + "renderToXlsWithPassword"+".xls";
        FileOutputStream fos = new FileOutputStream(outFile);
        renderer.render(data, "xxx:xls", fos);
        fos.close();
    }
}
