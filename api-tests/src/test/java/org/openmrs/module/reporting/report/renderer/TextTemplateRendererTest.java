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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Tests the TextTemplateRenderer
 */
public class TextTemplateRendererTest extends BaseModuleContextSensitiveTest {

	@Autowired
	PatientService patientService;

	@Before
	// This is needed due to a change to standardTestDataset in the OpenMRS 2.2 release that changed person 6 birth year from 2007 to 1975
	public void setup() {
		Patient p = patientService.getPatient(6);
		p.setBirthdate(DateUtil.getDateTime(2007, 5, 27));
		patientService.savePatient(p);
	}

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
