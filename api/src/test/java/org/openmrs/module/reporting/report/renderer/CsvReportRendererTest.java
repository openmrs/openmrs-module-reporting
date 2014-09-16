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

import org.junit.Test;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Verify that exported CSV files are consistent with http://tools.ietf.org/html/rfc4180#page-2
 */
public class CsvReportRendererTest extends BaseModuleContextSensitiveTest {

    @Autowired
    ReportDefinitionService reportDefinitionService;

    @Test
    public void testCsvStandardBehavior() throws Exception {
        ReportData data = new ReportData();
		SimpleDataSet dataSet = new SimpleDataSet(null, null);
		dataSet.addColumnValue(0, new DataSetColumn("PATIENT_ID", "PATIENT_ID", Integer.class), 2);
		dataSet.addColumnValue(0, new DataSetColumn("WITHQUOTE", "WITHQUOTE", String.class), "Say \"What?\"");
		data.getDataSets().put("data", dataSet);

		CsvReportRenderer renderer = new CsvReportRenderer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        renderer.render(data, "", out);
        // should be as follows (with \r\n line breaks):
        //   "PATIENT_ID","WITHQUOTE"
        //   "2","Say ""What?"""
        assertThat(out.toString(), is("\"PATIENT_ID\",\"WITHQUOTE\"\r\n\"2\",\"Say \"\"What?\"\"\"\r\n"));
    }

}
