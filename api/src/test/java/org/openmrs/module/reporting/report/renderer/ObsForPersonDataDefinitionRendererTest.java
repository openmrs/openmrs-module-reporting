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
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsClassLoader;

/**
 * Tests rendering for ObsForPersonDataDefinition
 */
public class ObsForPersonDataDefinitionRendererTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	@Test
	public void evaluate_shouldReturnMostRecentObsForPerson() throws Exception {
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("7"));
		
		ObsForPersonDataDefinition dataDefinition = new ObsForPersonDataDefinition();
		dataDefinition.setWhich(TimeQualifier.LAST);
		dataDefinition.setQuestion(Context.getConceptService().getConcept(5089));
		
		EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(dataDefinition, context);
		Assert.assertTrue(pd.getData().get(7) instanceof Obs);
		
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("Test Report");
		
		PatientDataSetDefinition dataset = new PatientDataSetDefinition();
		dataset.addColumn("obs", dataDefinition, (String) null, null);
		reportDefinition.addDataSetDefinition("obs", dataset, null);
		
		final ReportDesign reportDesign = new ReportDesign();
		reportDesign.setName("TestDesign");
		reportDesign.setReportDefinition(reportDefinition);
		reportDesign.setRendererType(TextTemplateRenderer.class);
		
		reportDesign.addPropertyValue(TextTemplateRenderer.TEMPLATE_TYPE, "Groovy");
		
		//EvaluationContext context = new EvaluationContext();
		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		ReportData reportData = rs.evaluate(reportDefinition, context);
		
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("ObsForPersonDataDefinitionRenderer.groovy");
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream(
		    "org/openmrs/module/reporting/report/renderer/ObsForPersonDataDefinitionRenderer.groovy");
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
		
		String xml = "<obs>WEIGHT(KG)</obs>";
		Assert.assertEquals(xml, renderedOutput);
	}
}
