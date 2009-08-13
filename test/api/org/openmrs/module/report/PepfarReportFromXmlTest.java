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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.cohort.definition.ProgramStateCohortDefinition;
import org.openmrs.module.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.cohort.definition.util.CohortExpressionParser;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.module.report.renderer.TsvReportRenderer;
import org.openmrs.module.report.service.ReportService;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.xml.OpenmrsCycleStrategy;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;

/**
 *
 */
public class PepfarReportFromXmlTest extends BaseContextSensitiveTest {
	
	Log log = LogFactory.getLog(getClass());
	
	DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
	
	Map<Parameter, Object> getUserEnteredParameters(Collection<Parameter> params) throws ParseException {
		Map<Parameter, Object> ret = new HashMap<Parameter, Object>();
		if (params != null) {
			for (Parameter p : params) {
				if (p.getName().equals("report.startDate"))
					ret.put(p, ymd.parse("2007-09-01"));
				else if (p.getName().equals("report.endDate"))
					ret.put(p, ymd.parse("2007-09-30"));
			}
		}
		return ret;
	}
	
	@Test
	public void shouldFromXml() throws Exception {
		executeDataSet("org/openmrs/report/include/PepfarReportTest.xml");
		
		StringBuilder xml = new StringBuilder();
		xml.append("<reportDefinition id=\"1\">\n");
		xml.append("    <name>PEPFAR report</name>\n");
		xml.append("	<description>\n");
		xml.append("		Sample monthly PEPFAR report, modelled after the lesotho one\n");
		xml.append("	</description>\n");
		xml.append("	<parameters class=\"java.util.ArrayList\">\n");
		xml
		        .append("		<parameter clazz=\"java.util.Date\"><name>report.startDate</name><label>When does the report period start?</label></parameter>/>\n");
		xml
		        .append("		<parameter clazz=\"java.util.Date\"><name>report.endDate</name><label>When does the report period end?</label></parameter>\n");
		xml
		        .append("		<parameter clazz=\"org.openmrs.Location\"><name>report.location</name><label>For which clinic is this report?</label></parameter>\n");
		xml.append("	</parameters>\n");
		xml.append("	<dataSets class=\"java.util.ArrayList\">\n");
		xml.append("		<dataSetDefinition class=\"org.openmrs.report.CohortDataSetDefinition\" name=\"Cohorts\">\n");
		xml.append("			<strategies class=\"java.util.LinkedHashMap\">\n");
		xml.append("				<entry>\n");
		xml.append("					<string>1.a</string>\n");
		xml.append("					<cohortDefinition class=\"org.openmrs.report.PatientSearch\">\n");
		xml.append("						<specification>[Male]</specification>\n");
		xml.append("					</cohortDefinition>\n");
		xml.append("				</entry>\n");
		xml.append("				<entry>\n");
		xml.append("					<string>1.b</string>\n");
		xml.append("					<cohortDefinition class=\"org.openmrs.report.PatientSearch\">\n");
		xml
		        .append("						<specification>[Male] and [Adult] and [EnrolledOnDate|untilDate=${report.startDate-1d}]</specification>\n");
		xml.append("					</cohortDefinition>\n");
		xml.append("				</entry>\n");
		xml.append("			</strategies>\n");
		xml.append("		</dataSetDefinition>\n");
		xml.append("	</dataSets>\n");
		xml.append("</reportDefinition>\n");
		//System.out.println("xml\n" + xml);
		
		// Try to get HIV PROGRAM, or else, just the first program
		Program hivProgram = Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM");
		if (hivProgram == null)
			hivProgram = Context.getProgramWorkflowService().getProgram(1);
		assertNotNull("Need at least one program defined to run this test", hivProgram);
		
		// Make sure we have all required PatientSearches
		setupPatientSearches();
		
		Serializer serializer = new Persister(new OpenmrsCycleStrategy());
		ReportDefinition schema = serializer.read(ReportDefinition.class, xml.toString());
		
		log.info("Creating EvaluationContext");
		EvaluationContext evalContext = new EvaluationContext();
		
		for (Map.Entry<Parameter, Object> e : getUserEnteredParameters(schema.getParameters()).entrySet()) {
			log.info("adding parameter value " + e.getKey());
			evalContext.addParameterValue(e.getKey().getName(), e.getValue());
		}
		
		ReportService rs = Context.getService(ReportService.class);
		ReportData data = rs.evaluate(schema, evalContext);
		
		TsvReportRenderer renderer = new TsvReportRenderer();
		//System.out.println("Rendering output as TSV:");
		//renderer.render(data, null, System.out);
	}
	
	@Test
	public void shouldBooleansInPatientSearch() throws Exception {
		executeDataSet("org/openmrs/report/include/ReportTests-patients.xml");
		setupPatientSearches();
		
		EvaluationContext evalContext = new EvaluationContext();
		evalContext.addParameterValue("report.startDate", ymd.parse("2007-09-01"));
		evalContext.addParameterValue("report.endDate", ymd.parse("2007-09-30"));
		
		CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
		
		CohortDefinition male = CohortExpressionParser.parse("[Male]");
		CohortDefinition female = CohortExpressionParser.parse("[Female]");
		CohortDefinition maleAndFemale = CohortExpressionParser.parse("[Male] and [Female]");
		CohortDefinition maleOrFemale = CohortExpressionParser.parse("[Male] or [Female]");
		int numMale = cds.evaluate(male, evalContext).size();
		int numFemale = cds.evaluate(female, evalContext).size();
		int numMaleAndFemale = cds.evaluate(maleAndFemale, evalContext).size();
		int numMaleOrFemale = cds.evaluate(maleOrFemale, evalContext).size();
		assertEquals("AND should be zero", 0, numMaleAndFemale);
		assertEquals("OR should be the sum", numMale + numFemale, numMaleOrFemale);
		
		CohortDefinition complex1 = CohortExpressionParser.parse("([Male] and [Child]) or ([Female] and [Adult])");
		assertNotSame("Should not be zero", 0, cds.evaluate(complex1, evalContext).size());
		
		CohortDefinition complex2 = CohortExpressionParser.parse("[Male] or [Female]");
		CohortDefinition complex3 = CohortExpressionParser.parse("(([Male] and [Child]) or [Female])");

		// this assertion will fail 15 years after 2008-07-01 because the birthdates are
		// set to that in the dataset for the two "children"
		assertNotSame("Complex2 and Complex3 should be different sizes", 
				cds.evaluate(complex2, evalContext).size(), 
				cds.evaluate(complex3, evalContext).size());
	}
	
	protected void setupPatientSearches() {
		
		CohortDefinitionService cds = Context.getService(CohortDefinitionService.class);
		
		GenderCohortDefinition maleFilter = new GenderCohortDefinition();
		maleFilter.setName("Male");
		maleFilter.setGender("M");
		cds.saveCohortDefinition(maleFilter);
		
		GenderCohortDefinition femaleFilter = new GenderCohortDefinition();
		femaleFilter.setName("Female");
		femaleFilter.setGender("F");
		cds.saveCohortDefinition(femaleFilter);

		AgeCohortDefinition adultOnDate = new AgeCohortDefinition();
		adultOnDate.setName("Adult");
		adultOnDate.setMinAge(15);
		cds.saveCohortDefinition(adultOnDate);
		
		AgeCohortDefinition childOnDate = new AgeCohortDefinition();
		childOnDate.setName("Child");
		childOnDate.setMaxAge(14);
		cds.saveCohortDefinition(childOnDate);

		ProgramStateCohortDefinition enrolledFilter = new ProgramStateCohortDefinition();
		enrolledFilter.setName("EnrolledOnDate");
		Program hivProgram = Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM");
		enrolledFilter.setProgram(hivProgram);
		enrolledFilter.addParameter(new Parameter("untilDate","untilDate",Date.class));
		cds.saveCohortDefinition(enrolledFilter);
	}
}
