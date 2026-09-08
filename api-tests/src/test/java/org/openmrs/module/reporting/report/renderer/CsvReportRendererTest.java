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
