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
package org.openmrs.module.dataset;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.dataset.definition.ObsDataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.report.ReportData;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.renderer.TsvReportRenderer;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.util.OpenmrsUtil;
import org.simpleframework.xml.Serializer;

/**
 *
 */
public class RowPerObsDatasetTest extends BaseModuleContextSensitiveTest {
	
	private Log log = LogFactory.getLog(getClass());
	
	/**
	 * TODO: fix this so it uses asserts instead of printing to the screen
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldSerialize() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/report/include/RowPerObsDatasetTest.xml");
		authenticate();
		
		EvaluationContext evalContext = new EvaluationContext();
		AgeCohortDefinition kids = new AgeCohortDefinition();
		
		Calendar today = new GregorianCalendar();
		
		// the first patient in the dataset has a birthday in 2007, so subtract that from
		// the current year for that patients age, then add one to make sure that patient 
		// is in the cohort (the second patient was born in 2000, so they shouldn't 
		// get counted)
		Integer maxAge = today.get(Calendar.YEAR) - 2007 + 1;
		kids.setMaxAge(maxAge);
		
		Cohort kidsCohort = 
			Context.getService(CohortDefinitionService.class).evaluate(kids, evalContext);
		log.info("Kids cohort = " + kidsCohort == null ? " null " : kidsCohort.getMemberIds());
		evalContext.setBaseCohort(kidsCohort);
		
		ObsDataSetDefinition definition = new ObsDataSetDefinition();
		definition.setName("Row per Obs");
		//commenting this out because serializing PatientSearches is not yet implemented
		//definition.setFilter(kids);
		definition.getQuestions().add(Context.getConceptService().getConcept(5089));
		
		ReportDefinition rs = new ReportDefinition();
		rs.setName("Testing row-per-obs");
		rs.setDescription("Tesing RowPerObsDataSet*");
		rs.addDataSetDefinition("test", definition, (String)null);
		
		Serializer serializer = OpenmrsUtil.getShortSerializer();
		StringWriter writer = new StringWriter();
		serializer.write(rs, writer);
		String xmlOutput = writer.toString();
		XMLAssert.assertXpathEvaluatesTo("5089", "//reportDefinition/dataSets/dataSetDefinition/questions/concept/@conceptId", xmlOutput);
		
		rs = (ReportDefinition) serializer.read(ReportDefinition.class, xmlOutput);
		assertEquals("Testing row-per-obs", rs.getName());
		assertEquals(1, rs.getDataSetDefinitions().size());
		
		ReportData data = 
			Context.getService(ReportService.class).evaluate(rs, evalContext);
		//System.out.println("Result=");
		
		StringWriter w = new StringWriter();
		new TsvReportRenderer().render(data, null, w);
		
		String expectedOutput = "\"patientId\"	\"question\"	\"questionConceptId\"	\"answer\"	\"answerConceptId\"	\"obsDatetime\"	\"encounterId\"	\"obsGroupId\"	\n\"2\"	\"WEIGHT\"	\"5089\"	\"100.0\"	\"\"	\"2005-01-01 00:00:00.0\"	\"1\"	\"\"	\n";
		// (This line was used to generate the above line of code)
		// TestUtil.printAssignableToSingleString(w.toString());
		
		assertEquals(expectedOutput, w.toString());
		
	}
	
}
