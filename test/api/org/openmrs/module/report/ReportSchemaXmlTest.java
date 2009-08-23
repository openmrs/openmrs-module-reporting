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
package org.openmrs.module.report;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.cohort.definition.ProgramStateCohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.test.BaseContextSensitiveTest;

/**
 * Tests the small class ReportDefinitionXml and its database accesses
 */
public class ReportSchemaXmlTest extends BaseContextSensitiveTest {
	
	Log log = LogFactory.getLog(getClass());
	
	/**
	 * Set up the database with the initial dataset before every test method in this class.
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		// Comment out when running test on underlying database instead of in-memory database.
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/report/include/ReportDefinitionXmlTest-initialData.xml");
		
		authenticate();
		
		GenderCohortDefinition maleDef = new GenderCohortDefinition();
		maleDef.setName("Male");
		maleDef.setGender("M");
		Context.getService(CohortDefinitionService.class).saveCohortDefinition(maleDef);
		
		
		AgeCohortDefinition adult = new AgeCohortDefinition();
		adult.setName("Adult");
		adult.setMinAge(15);
		Context.getService(CohortDefinitionService.class).saveCohortDefinition(adult);

		ProgramStateCohortDefinition enrolledFilter = new ProgramStateCohortDefinition();
		enrolledFilter.setName("EnrolledOnDate");
		Program hivProgram = Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM");
		enrolledFilter.setProgram(hivProgram);
		enrolledFilter.addParameter(new Parameter("untilDate","untilDate",Date.class));
		Context.getService(CohortDefinitionService.class).saveCohortDefinition(enrolledFilter);

	}
	
	/**
	 * Saves a new ReportDefinitionXml in database. Gets it. Then Deletes it. Tests for successful save,
	 * get, and delete.
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldSaveGetDeleteReportDefinition() throws Exception {
		
		StringBuilder xml = new StringBuilder();
		xml.append("<reportDefinition id=\"1\" reportDefinitionId=\"1\">\n");
		xml.append("    <name>PEPFAR report</name>\n");
		xml.append("	<description>\n");
		xml.append("		Sample monthly PEPFAR report, modeled after the lesotho one\n");
		xml.append("	</description>\n");
		xml.append("	<parameters class=\"java.util.ArrayList\">\n");
		xml
		        .append("		<parameter type=\"java.util.Date\"><name>report.startDate</name><label>When does the report period start?</label></parameter>/>\n");
		xml
		        .append("		<parameter type=\"java.util.Date\"><name>report.endDate</name><label>When does the report period end?</label></parameter>\n");
		xml
		        .append("		<parameter type=\"org.openmrs.Location\"><name>report.location</name><label>For which clinic is this report?</label></parameter>\n");
		xml.append("	</parameters>\n");
		xml.append("	<dataSets class=\"java.util.ArrayList\">\n");
		xml.append("		<dataSetDefinition class=\"org.openmrs.dataset.CohortDataSetDefinition\" name=\"Cohorts\">\n");
		xml.append("			<strategies class=\"java.util.LinkedHashMap\">\n");
		xml.append("				<entry>\n");
		xml.append("					<string>1.a</string>\n");
		xml.append("					<cohort class=\"org.openmrs.report.PatientSearch\">\n");
		xml.append("						<specification>[Male]</specification>\n");
		xml.append("					</cohort>\n");
		xml.append("				</entry>\n");
		xml.append("				<entry>\n");
		xml.append("					<string>1.b</string>\n");
		xml.append("					<cohort class=\"org.openmrs.report.PatientSearch\">\n");
		xml
		        .append("						<specification>[Male] and [Adult] and [EnrolledOnDate|untilDate=${report.startDate - 1d}]</specification>\n");
		xml.append("					</cohort>\n");
		xml.append("				</entry>\n");
		xml.append("			</strategies>\n");
		xml.append("		</dataSetDefinition>\n");
		xml.append("	</dataSets>\n");
		xml.append("</reportDefinition>\n");
		
		
		// FIXME 
		// TODO These tests should be in separate methods
		Assert.fail("ReportDefinitionXml has been removed and does not have a suitable replacement");
		
		// create and check the report schema object
		//ReportDefinitionXml reportDefinitionXml = new ReportDefinitionXml();
		//reportDefinitionXml.setXml(xml.toString());		
		//assertEquals(new Integer(1), reportDefinitionXml.getReportDefinitionId());
		//assertEquals(xml.toString(), reportDefinitionXml.getXml());		
		//ReportService rs = (ReportService) Context.getService(ReportService.class);
		//rs.saveReportDefinitionXml(reportDefinitionXml);
		
		Context.clearSession();

		// FIXME 
		// TODO These tests should be in separate methods
		Assert.fail("ReportDefinitionXml has been removed and does not have a suitable replacement");
		
		//ReportDefinitionXml reportDefinitionXmlFromDB = rs.getReportDefinitionXml(1);	
		//assertNotNull("The schema xml was not saved correctly, none found in the db", reportDefinitionXmlFromDB);
		//assertEquals(xml.toString(), reportDefinitionXmlFromDB.getXml());		
		//assertEquals(new Integer(1), reportDefinitionXmlFromDB.getReportDefinitionId());
		//assertTrue("The saved object and the actual object are not calling themselves equal", reportDefinitionXml.equals(reportDefinitionXmlFromDB));
	
		// delete the just created report schema xml object
		//rs.deleteReportDefinitionXml(reportDefinitionXmlFromDB);
		
		// FIXME 
		// TODO These tests should be in separate methods
		Assert.fail("ReportDefinitionXml has been removed and does not have a suitable replacement");
		
		// try to fetch that deleted xml object, expect null
		//ReportDefinitionXml deletedXml = rs.getReportDefinitionXml(1);
		//assertNull("The deleted xml object should be null", deletedXml);
		
	}
	
	/**
	 * Creates a ReportDefinitionXml such as in {@link #testSaveGetDeleteReportDefinition()}, then changes
	 * it, updates it in the database, and tests to see if the update is successful.
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldUpdateReportDefinitionXml() throws Exception {
		
		StringBuilder xml = new StringBuilder();
		xml.append("<reportDefinition id=\"2\" reportDefinitionId=\"2\">\n");
		xml.append("    <name>PEPFAR report</name>\n");
		xml.append("	<description>\n");
		xml.append("		Sample monthly PEPFAR report, modeled after the lesotho one\n");
		xml.append("	</description>\n");
		xml.append("	<parameters class=\"java.util.ArrayList\">\n");
		xml
		        .append("		<parameter type=\"java.util.Date\"><name>report.startDate</name><label>When does the report period start?</label></parameter>/>\n");
		xml
		        .append("		<parameter type=\"java.util.Date\"><name>report.endDate</name><label>When does the report period end?</label></parameter>\n");
		xml
		        .append("		<parameter type=\"org.openmrs.Location\"><name>report.location</name><label>For which clinic is this report?</label></parameter>\n");
		xml.append("	</parameters>\n");
		xml.append("	<dataSets class=\"java.util.ArrayList\">\n");
		xml.append("		<dataSetDefinition class=\"org.openmrs.dataset.CohortDataSetDefinition\" name=\"Cohorts\">\n");
		xml.append("			<strategies class=\"java.util.LinkedHashMap\">\n");
		xml.append("				<entry>\n");
		xml.append("					<string>1.a</string>\n");
		xml.append("					<cohort class=\"org.openmrs.report.PatientSearch\">\n");
		xml.append("						<specification>[Male]</specification>\n");
		xml.append("					</cohort>\n");
		xml.append("				</entry>\n");
		xml.append("				<entry>\n");
		xml.append("					<string>1.b</string>\n");
		xml.append("					<cohort class=\"org.openmrs.report.PatientSearch\">\n");
		xml.append("						<specification>[Male] and [Adult]</specification>\n");
		xml.append("					</cohort>\n");
		xml.append("				</entry>\n");
		xml.append("			</strategies>\n");
		xml.append("		</dataSetDefinition>\n");
		xml.append("	</dataSets>\n");
		xml.append("</reportDefinition>\n");
		
		
		// FIXME 
		// TODO These tests should be in separate methods
		Assert.fail("ReportDefinitionXml has been removed and does not have a suitable replacement");
				
		// create and check the report schema object
		//ReportDefinitionXml reportDefinitionXml = new ReportDefinitionXml();
		//reportDefinitionXml.setXml(xml.toString());
		//assertEquals(xml.toString(), reportDefinitionXml.getXml());
		
		//ReportService rs = (ReportService) Context.getService(ReportService.class);
		//rs.saveReportDefinitionXml(reportDefinitionXml);
		
		//ReportDefinitionXml reportDefinitionXmlFromDB = rs.getReportDefinitionXml(2);
		
		// Get an extra object with the same id just to mess things up.
		//ReportDefinitionXml reportDefinitionXmlJodion = rs.getReportDefinitionXml(2);
		
		//assertTrue("The saved object and the actual object are not calling themselves equal", reportDefinitionXml.equals(reportDefinitionXmlFromDB));
		
		//assertEquals(xml.toString(), reportDefinitionXmlFromDB.getXml());
		
		// Create a slightly different xml.
		StringBuilder xml2 = new StringBuilder();
		xml2.append("<reportDefinition id=\"2\" reportDefinitionId=\"2\">\n");
		xml2.append("    <name>PEPFAR report updated</name>\n");
		xml2.append("	<description>\n");
		xml2.append("		Sample monthly PEPFAR report changed again\n");
		xml2.append("	</description>\n");
		xml2.append("	<parameters class=\"java.util.ArrayList\">\n");
		xml2
		        .append("		<parameter type=\"java.util.Date\"><name>report.startDate</name><label>When does the report period start?</label></parameter>/>\n");
		xml2
		        .append("		<parameter type=\"java.util.Date\"><name>report.endDate</name><label>When does the report period end?</label></parameter>\n");
		xml2
		        .append("		<parameter type=\"org.openmrs.Location\"><name>report.location</name><label>For which clinic is this report?</label></parameter>\n");
		xml2.append("	</parameters>\n");
		xml2.append("	<dataSets class=\"java.util.ArrayList\">\n");
		xml2.append("		<dataSetDefinition class=\"org.openmrs.dataset.CohortDataSetDefinition\" name=\"Cohorts\">\n");
		xml2.append("			<strategies class=\"java.util.LinkedHashMap\">\n");
		xml2.append("				<entry>\n");
		xml2.append("					<string>1.a</string>\n");
		xml2.append("					<cohort class=\"org.openmrs.report.PatientSearch\">\n");
		xml2.append("						<specification>[Male]</specification>\n");
		xml2.append("					</cohort>\n");
		xml2.append("				</entry>\n");
		xml2.append("				<entry>\n");
		xml2.append("					<string>1.b</string>\n");
		xml2.append("					<cohort class=\"org.openmrs.report.PatientSearch\">\n");
		xml2.append("						<specification>[Male] and [Adult]</specification>\n");
		xml2.append("					</cohort>\n");
		xml2.append("				</entry>\n");
		xml2.append("				<entry>\n");
		xml2.append("					<string>1.c</string>\n");
		xml2.append("					<cohort class=\"org.openmrs.report.PatientSearch\">\n");
		xml2.append("						<specification>[Adult]</specification>\n");
		xml2.append("					</cohort>\n");
		xml2.append("				</entry>\n");
		xml2.append("			</strategies>\n");
		xml2.append("		</dataSetDefinition>\n");
		xml2.append("	</dataSets>\n");
		xml2.append("</reportDefinition>\n");

		
		// FIXME 
		// TODO These tests should be in separate methods
		Assert.fail("ReportDefinitionXml has been removed and does not have a suitable replacement");
		
		// Update the ReportDefinitionXml with a different [name, description, and] xml.
		//reportDefinitionXmlFromDB.setXml(xml2.toString());
		//String newName = "PEPFAR Report with a new name.";
		//String newDescription = "PEPFAR Report with a new description.";
		String newName = "PEPFAR report updated";
		String newDescription = "Sample monthly PEPFAR report changed again";
		//reportDefinitionXmlFromDB.setName(newName);
		//reportDefinitionXmlFromDB.setDescription(newDescription);
		//rs.saveReportDefinitionXml(reportDefinitionXmlFromDB);
		
		// Retrieve the updated ReportDefinitionXml from database.
		//ReportDefinitionXml reportDefinitionXmlUpdateFromDB = rs.getReportDefinitionXml(reportDefinitionXmlFromDB.getReportDefinitionId());
		
		// Were the [name, description, and] xml really updated?
		//assertEquals(xml2.toString(), reportDefinitionXmlUpdateFromDB.getXml());
		//assertEquals(newName, reportDefinitionXmlUpdateFromDB.getName());
		//assertEquals(newDescription, reportDefinitionXmlUpdateFromDB.getDescription());
	}
	
}
