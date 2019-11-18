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
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
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
public class JSONReportRendererTest extends BaseModuleContextSensitiveTest {

    @Autowired
    ReportDefinitionService reportDefinitionService;

    @Test
    //Json test with only one object
    public void testJSONStandardBehavior() throws Exception {
        ReportData data = new ReportData();
		SimpleDataSet dataSet = new SimpleDataSet(null, null);
		dataSet.addColumnValue(0, new DataSetColumn("PATIENT_ID", "PATIENT_ID", Integer.class), "2");
		dataSet.addColumnValue(0, new DataSetColumn("WITHQUOTE", "WITHQUOTE", String.class), "\"Say What?\"");
		data.getDataSets().put("data", dataSet);

		JSONReportRenderer renderer = new JSONReportRenderer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        renderer.render(data, "", out);

        assertThat(out.toString(), is("\"PATIENT_ID\",\"WITHQUOTE\"\r\n\"2\",\"Say \"\"What?\"\"\"\r\n"));
    }

    @Test
    //Json test with only one object
    public void testJSONRenderer() throws Exception {
        EvaluationContext context = new EvaluationContext();
        ReportDefinition report = new ReportDefinition();
        SqlDataSetDefinition maleDetails = new SqlDataSetDefinition();
        maleDetails.setName("maleDetails");
        maleDetails.setSqlQuery("select p.patient_id, n.gender, n.birthdate from patient p, person n where p.patient_id = n.person_id and n.gender = 'M'");
        report.addDataSetDefinition("malePatients", maleDetails, null);
        ReportData data = Context.getService(ReportDefinitionService.class).evaluate(report,context);


        JSONReportRenderer renderer = new JSONReportRenderer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        renderer.render(data, "", out);

        assertThat(out.toString(), is("\"PATIENT_ID\",\"WITHQUOTE\"\r\n\"2\",\"Say \"\"What?\"\"\"\r\n"));
    }

}
