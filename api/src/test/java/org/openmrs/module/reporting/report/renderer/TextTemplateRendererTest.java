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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimplePatientDataSetDefinition;
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
 * Tests the TextTemplateRenderer
 */
public class TextTemplateRendererTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void shouldRenderVariableReplacementTemplate() throws Exception {
		shouldRenderTemplate("VariableReplacementTemplate.txt", null);
	}
	
	@Test
	public void shouldRenderGroovyTemplate() throws Exception {
		shouldRenderTemplate("GroovyTemplate.groovy", "groovy");
	}
	
	//@Test
	public void shouldRenderVelocityTemplate() throws Exception {
		shouldRenderTemplate("VelocityTemplate.vm", "velocity");
	}
	
	@Test
	public void shouldRenderJavaScriptTemplate() throws Exception {
		shouldRenderTemplate("JavaScriptTemplate.js", "JavaScript");
	}
	
	private void shouldRenderTemplate(String templateName, String scriptEngineName) throws Exception {
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("Test Report");
		reportDefinition.addParameter(new Parameter("programState", "Which program state?", ProgramWorkflowState.class));
		
		final ReportDesign reportDesign = new ReportDesign();
		reportDesign.setName("TestDesign");
		reportDesign.setReportDefinition(reportDefinition);
		reportDesign.setRendererType(TextTemplateRenderer.class);
		if (scriptEngineName != null) {
			reportDesign.addPropertyValue(TextTemplateRenderer.SCRIPT_ENGINE_NAME, scriptEngineName);
			
			SimplePatientDataSetDefinition allPatients = new SimplePatientDataSetDefinition("allPatients", "");
			allPatients.addPatientProperty("patientId");
			allPatients.addPatientProperty("gender");
			allPatients.addPatientProperty("birthdate");
			reportDefinition.addDataSetDefinition("allPatients", allPatients, null);
		} else {
			//variable replacement templates support only one row datasets
			GenderCohortDefinition males = new GenderCohortDefinition();
			males.setName("Males");
			males.setMaleIncluded(true);
			
			GenderCohortDefinition females = new GenderCohortDefinition();
			females.setName("Females");
			females.setFemaleIncluded(true);
			
			CohortCrossTabDataSetDefinition genderDsd = new CohortCrossTabDataSetDefinition();
			genderDsd.addColumn("males", males, null);
			genderDsd.addColumn("females", females, null);
			reportDefinition.addDataSetDefinition("genders", genderDsd, null);
		}
		
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("programState",
		    Context.getProgramWorkflowService().getStateByUuid("92584cdc-6a20-4c84-a659-e035e45d36b0"));
		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		ReportData reportData = rs.evaluate(reportDefinition, context);
		
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName(templateName);
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream(
		    "org/openmrs/module/reporting/report/renderer/" + templateName);
		resource.setContents(IOUtils.toByteArray(is));
		IOUtils.closeQuietly(is);
		reportDesign.addResource(resource);
		
		TextTemplateRenderer renderer = new TextTemplateRenderer() {
			
			public ReportDesign getDesign(String argument) {
				return reportDesign;
			}
		};
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		renderer.render(reportData, "ReportData", baos);
		
		String xml = "<?xml version=\"1.0\"?>" + "<dataset>" + "	<rows>"
		        + "		<row><patientId>2</patientId><gender>M</gender><birthdate>1975-04-08 00:00:00.0</birthdate></row>"
		        + "		<row><patientId>6</patientId><gender>M</gender><birthdate>2007-05-27 00:00:00.0</birthdate></row>"
		        + "		<row><patientId>7</patientId><gender>F</gender><birthdate>1976-08-25 00:00:00.0</birthdate></row>"
		        + "		<row><patientId>8</patientId><gender>F</gender><birthdate></birthdate></row>" + "	</rows>"
		        + "</dataset>";
		
		xml = scriptEngineName != null ? StringUtils.deleteWhitespace(xml) : "Males=2Females=2";
		Assert.assertEquals(xml, StringUtils.deleteWhitespace(baos.toString()));
	}
}
