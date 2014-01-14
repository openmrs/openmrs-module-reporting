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
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
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
        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.setName("Testing");
        reportDefinition.addDataSetDefinition("testing", new SqlDataSetDefinition("one", "sql", "select patient_id, 'Say \"What?\"' as withquote from patient where patient_id=2"), null);

        CsvReportRenderer renderer = new CsvReportRenderer();
        ReportData data = reportDefinitionService.evaluate(reportDefinition, new EvaluationContext());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        renderer.render(data, "", out);
        // should be as follows (with \r\n line breaks):
        //   "PATIENT_ID","WITHQUOTE"
        //   "2","Say ""What?"""
        assertThat(out.toString(), is("\"PATIENT_ID\",\"WITHQUOTE\"\r\n\"2\",\"Say \"\"What?\"\"\"\r\n"));
    }

}
