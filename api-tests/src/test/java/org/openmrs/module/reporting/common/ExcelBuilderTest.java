/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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