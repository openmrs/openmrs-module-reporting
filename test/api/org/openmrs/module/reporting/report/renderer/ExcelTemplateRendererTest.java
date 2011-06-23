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

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimplePatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsClassLoader;

/**
 * Supports rendering a report to Excel
 */
public class ExcelTemplateRendererTest extends BaseModuleContextSensitiveTest {

	@Test
	public void shouldRenderToExcelTemplate() throws Exception {
		
		// We first set up a report with 2 indicators, numbered 1 and 2
		
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setName("Males");
		males.setMaleIncluded(true);
		
		GenderCohortDefinition females = new GenderCohortDefinition();
		females.setName("Females");
		females.setFemaleIncluded(true);
		
		ReportDefinition report = new ReportDefinition();
		report.setName("Test Report");
		
		CohortCrossTabDataSetDefinition genderDsd = new CohortCrossTabDataSetDefinition();
		genderDsd.addColumn("males", males, null);
		genderDsd.addColumn("females", females, null);
		report.addDataSetDefinition("genders", genderDsd, null);
		
		SimplePatientDataSetDefinition allPatients = new SimplePatientDataSetDefinition("allPatients", "");
		allPatients.addPatientProperty("patientId");
		allPatients.addPatientProperty("gender");
		allPatients.addPatientProperty("birthdate");
		report.addDataSetDefinition("allPatients", allPatients, null);
		
		SqlDataSetDefinition femaleDetails = new SqlDataSetDefinition();
		femaleDetails.setName("femaleDetails");
		femaleDetails.setSqlQuery("select p.patient_id, n.gender, n.birthdate from patient p, person n where p.patient_id = n.person_id and n.gender = 'F'");
		report.addDataSetDefinition("femalePatients", femaleDetails, null);
		
		SqlDataSetDefinition maleDetails = new SqlDataSetDefinition();
		maleDetails.setName("maleDetails");
		maleDetails.setSqlQuery("select p.patient_id, n.gender, n.birthdate from patient p, person n where p.patient_id = n.person_id and n.gender = 'M'");
		report.addDataSetDefinition("malePatients", maleDetails, null);
		
		// Next, we set up the ReportDesign and ReportDesignResource files for the renderer
		
		final ReportDesign design = new ReportDesign();
		design.setName("TestDesign");
		design.setReportDefinition(report);
		design.setRendererType(ExcelTemplateRenderer.class);
		
		Properties props = new Properties();
		props.put("repeatSheet1Row2", "allPatients,3");
		props.put("repeatSheet2Row9", "femalePatients");
		props.put("repeatSheet2Column4", "malePatients");
		props.put("repeatSheet3", "allPatients");
		
		design.setProperties(props);
		
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("template.xls");
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream("org/openmrs/module/reporting/report/renderer/ExcelTemplateRendererTest.xls");
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
		
		CsvReportRenderer csvRenderer = new CsvReportRenderer();
		csvRenderer.render(data, "thedata", System.out);
		
		FileOutputStream fos = new FileOutputStream("/tmp/test.xls"); // You will need to change this if you have no /tmp directory
		renderer.render(data, "xxx:xls", fos);
		fos.close();
	}

}