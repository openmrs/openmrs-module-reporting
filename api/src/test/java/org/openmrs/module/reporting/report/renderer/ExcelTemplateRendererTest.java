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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimplePatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
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
		report.addParameter(new Parameter("programState", "Which program state?", ProgramWorkflowState.class));
		
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
		props.put("repeatingSections", "sheet:1,row:6-8,dataset:allPatients | sheet:2,column:4,dataset:malePatients | sheet:3,dataset:allPatients");

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
		context.addParameterValue("programState", Context.getProgramWorkflowService().getStateByUuid("92584cdc-6a20-4c84-a659-e035e45d36b0"));
		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		ReportData data = rs.evaluate(report, context);

		String outFile = System.getProperty("java.io.tmpdir") + File.separator + "excelTemplateRendererTest.xls";
		FileOutputStream fos = new FileOutputStream(outFile);
		renderer.render(data, "xxx:xls", fos);
		fos.close();
	}

}