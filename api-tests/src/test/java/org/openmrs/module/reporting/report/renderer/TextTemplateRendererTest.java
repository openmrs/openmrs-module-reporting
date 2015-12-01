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
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimplePatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
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
		shouldRenderTemplate("GroovyTemplate.txt", "Groovy");
	}
	
	@Test
	public void shouldRenderVelocityTemplate() throws Exception {
		shouldRenderTemplate("VelocityTemplate.vm", "Velocity");
	}
	
	private void shouldRenderTemplate(String templateName, String templateType) throws Exception {
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("Test Report");
		
		SimplePatientDataSetDefinition allPatients = new SimplePatientDataSetDefinition("allPatients", "");
		allPatients.addPatientProperty("patientId");
		allPatients.addPatientProperty("gender");
		allPatients.addPatientProperty("birthdate");
		reportDefinition.addDataSetDefinition("allPatients", allPatients, null);
		
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
		
		final ReportDesign reportDesign = new ReportDesign();
		reportDesign.setName("TestDesign");
		reportDesign.setReportDefinition(reportDefinition);
		reportDesign.setRendererType(TextTemplateRenderer.class);
		
		if (templateType != null) {
			reportDesign.addPropertyValue(TextTemplateRenderer.TEMPLATE_TYPE, templateType);
		}
		
		EvaluationContext context = new EvaluationContext();
		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		ReportData reportData = rs.evaluate(reportDefinition, context);
		
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName(templateName);
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream("org/openmrs/module/reporting/report/renderer/" + templateName);
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
		String renderedOutput = StringUtils.deleteWhitespace(baos.toString());
		
		String xml = "<?xml version=\"1.0\"?>" + "<dataset>" + "	<rows>"
		        + "		<row><patientId>2</patientId><gender>M</gender><birthdate>08/Apr/1975</birthdate></row>"
		        + "		<row><patientId>6</patientId><gender>M</gender><birthdate>27/May/2007</birthdate></row>"
		        + "		<row><patientId>7</patientId><gender>F</gender><birthdate>25/Aug/1976</birthdate></row>"
		        + "		<row><patientId>8</patientId><gender>F</gender><birthdate></birthdate></row>" + "	</rows>"
		        + "</dataset>";
		
		xml = templateType != null ? StringUtils.deleteWhitespace(xml) : "Males=2Females=2";
		Assert.assertEquals(xml, renderedOutput);
	}
}
