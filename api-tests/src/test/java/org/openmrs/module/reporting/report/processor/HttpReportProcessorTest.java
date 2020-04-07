
/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.reporting.report.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimplePatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Priority;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.test.BaseModuleContextSensitiveTest;


public class HttpReportProcessorTest extends BaseModuleContextSensitiveTest {

	
	@Test
	public void testHttpUrlConnection() throws Exception {

		URL url = new URL("http://www.example.com/docs/resource1.html");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		assertTrue(url.openConnection() instanceof HttpURLConnection);
	}
	
     @Test
     public void testHttPostConnection() throws Exception {
    	 
    	 URL url = new URL("http://www.example.com/docs/resource1.html");
    	 HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    	 connection.setRequestMethod("POST");
    	 assertEquals(connection.getRequestMethod(),"POST");
	}
     
     @Test
     public void testHttpReportContent() throws Exception{
    	 
    	// Generate a report definition to add in the data set definitions for
 		// generating a report
 		ReportDefinition reportDefinition = new ReportDefinition();
 		reportDefinition.setName("Test processor Report");

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

 		RenderingMode mode = new RenderingMode(new CsvReportRenderer(), "CSV", null, 50);

 		ReportRequest request = new ReportRequest(new Mapped<ReportDefinition>(reportDefinition, null), null, mode,
 				Priority.HIGHEST, null);
 		Report report = Context.getService(ReportService.class).runReport(request);
 		String addContent = new String(report.getRenderedOutput());
 		
         //content of a rendered report can be held via an httpconnection to a url
    	 URL url = new URL("http://www.example.com/docs/resource1.html");
    	 HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    	 connection.setRequestProperty("Content-Type","addContent");
    	 assertNotNull(connection.getRequestProperty("Content-Type"));
    	 
    	 
     }
     

}
