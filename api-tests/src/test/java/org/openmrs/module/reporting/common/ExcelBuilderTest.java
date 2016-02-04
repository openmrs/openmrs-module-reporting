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
package org.openmrs.module.reporting.common;

import org.junit.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Testing the ExcelBuilder class.
 */
public class ExcelBuilderTest extends BaseModuleContextSensitiveTest {

	protected Log log = LogFactory.getLog(this.getClass());

	@Test
	public void shouldBuildAnExcelWorkbook() throws Exception {

		ExcelBuilder excelBuilder = new ExcelBuilder();
		Assert.assertNotNull(excelBuilder.getWorkbook());

		excelBuilder.newSheet("SheetOne");
		Assert.assertEquals("SheetOne", excelBuilder.getCurrentSheet().getSheetName());

		excelBuilder.addCell("Row One Cell One");
		excelBuilder.addCell("Row One Cell Two", "bold");
		Assert.assertEquals("Row One Cell One, Row One Cell Two", ExcelUtil.formatRow(excelBuilder.getCurrentRow()));
		excelBuilder.nextRow();
		excelBuilder.addCell("Row Two Cell One");
		excelBuilder.addCell("Row Two Cell Two", "bold");
		Assert.assertEquals("Row Two Cell One, Row Two Cell Two", ExcelUtil.formatRow(excelBuilder.getCurrentRow()));

		excelBuilder.newSheet("SheetTwo");
		Assert.assertEquals("SheetTwo", excelBuilder.getCurrentSheet().getSheetName());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		excelBuilder.write(baos);
		Assert.assertTrue(baos.size() > 0);
	}

	@Test
	public void shouldSupportMoreThan4000StyledCells() throws Exception {
		ExcelBuilder excelBuilder = new ExcelBuilder();
		for (int i=0; i<10000; i++) {
			excelBuilder.addCell("Row " + i, "bold");
			excelBuilder.addCell(new Date());
			excelBuilder.addCell("Value " + i, "italic,underline");
			excelBuilder.nextRow();
		}
		String outFile = System.getProperty("java.io.tmpdir") + File.separator + "shouldSupportMoreThan4000StyledCells.xls";
		FileOutputStream fos = new FileOutputStream(outFile);
		excelBuilder.write(fos);
		fos.close();
	}

    @Test
    public void shouldSupportEmptyStringCellContents() throws Exception {
        ExcelBuilder excelBuilder = new ExcelBuilder();
        excelBuilder.newSheet("SheetOne");

        excelBuilder.addCell("");

        assertThat(ExcelUtil.formatRow(excelBuilder.getCurrentRow()), is(""));
    }

}