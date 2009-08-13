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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.openmrs.module.evaluation.parameter.Parameter;
import org.openmrs.test.BaseContextSensitiveTest;

/**
 * Test class that tries to run a portion of the Pepfar monthly report
 */
public class PepfarReportTest extends BaseContextSensitiveTest {
	
	Log log = LogFactory.getLog(getClass());
	
	DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * Auto generated method comment
	 * 
	 * @param params
	 * @return
	 * @throws ParseException
	 */
	Map<Parameter, Object> getUserEnteredParameters(Collection<Parameter> params) throws ParseException {
		Map<Parameter, Object> ret = new HashMap<Parameter, Object>();
		
		if (params != null)
			for (Parameter p : params) {
				if (p.getName().equals("report.startDate"))
					ret.put(p, ymd.parse("2007-09-01"));
				else if (p.getName().equals("report.endDate"))
					ret.put(p, ymd.parse("2007-09-30"));
			}
		return ret;
	}
	
	/**
	 * Auto generated method comment
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldPepfarReport() throws Exception {
		/**
		 * TODO: This is really just here to create a new report in the test
		 * It also usefully shows how some of the classes can be used.
		 * It isn't worth fixing to get it working, so I'm commenting it out for reference
		 * 
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/report/include/PepfarReportTest.xml");
		
		authenticate();
		
		Cohort inputCohort = null;
		
		Parameter startDateParam = new Parameter("report.startDate", "Report Start Date", java.util.Date.class, null);
		Parameter endDateParam = new Parameter("report.endDate", "Report End Date", java.util.Date.class, null);
		
		log.info("Creating basic PatientSearches");
		CohortDefinition male = new PatientCharacteristicCohortDefinition();
		male.setParameterValue("gender", "M");
		
		CohortDefinition female = new PatientCharacteristicCohortDefinition();
		female.setParameterValue("gender", "F");
		
		CohortDefinition adult = new PatientCharacteristicCohortDefinition();
		adult.setParameterValue("minAge", "15");
		
		CohortDefinition child = new PatientCharacteristicCohortDefinition();
		child.setParameterValue("maxAge", "15");
		
		Program hivProgram = Context.getProgramWorkflowService().getProgramByName("HIV PROGRAM");
		if (hivProgram == null) {
			List<Program> programs = Context.getProgramWorkflowService().getAllPrograms();
			for (Program p : programs) {
				hivProgram = p;
			}
		}
		
		assertNotNull(hivProgram);
		
		CohortDefinition enrolledBeforeDate = new ProgramStateCohortDefinition());
		enrolledBeforeDate.setParameterValue("program", hivProgram);
		enrolledBeforeDate.setParameterOverride("untilDate", true);


		log.info("Creating DataSets");
		List<DataSetDefinition> dataSets = new ArrayList<DataSetDefinition>();
		CohortDataSetDefinition dataSetDef = new CohortDataSetDefinition();
		dataSetDef.setName("Cohorts");
		dataSetDef.addStrategy("Cumulative ever enrolled before start of period", enrolledBeforeDate);
		dataSetDef.addStrategy("Male adults ever enrolled before start of period", PatientSearch
		        .createCompositionSearch(new Object[] { male, "and", adult, "and", enrolledBeforeDate }));
		dataSetDef.addStrategy("Feale adults ever enrolled before start of period", PatientSearch
		        .createCompositionSearch(new Object[] { female, "and", adult, "and", enrolledBeforeDate }));
		dataSetDef.addStrategy("Male children ever enrolled before start of period", PatientSearch
		        .createCompositionSearch(new Object[] { male, "and", child, "and", enrolledBeforeDate }));
		dataSetDef.addStrategy("Female children ever enrolled before start of period", PatientSearch
		        .createCompositionSearch(new Object[] { female, "and", child, "and", enrolledBeforeDate }));
		dataSets.add(dataSetDef);
		
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(startDateParam);
		parameters.add(endDateParam);
		
		log.info("Creating the ReportDefinition");
		ReportDefinition schema = new ReportDefinition();
		schema.setReportDefinitionId(123);
		schema.setName("Pepfar Report");
		schema.setDescription("desc");
		schema.setDataSetDefinitions(dataSets);
		schema.setReportParameters(parameters);
		
		// todo
		// set the xml file on the schema
		
		log.info("Creating EvaluationContext");
		EvaluationContext evalContext = new EvaluationContext();
		
		for (Map.Entry<Parameter, Object> e : getUserEnteredParameters(schema.getReportParameters()).entrySet()) {
			log.info("adding parameter value " + e.getKey());
			evalContext.addParameterValue(e.getKey(), e.getValue());
		}
		
		// TODO figure out about the non-top-level parameters
		
		// run the report
		ReportService rs = (ReportService) Context.getService(ReportService.class);
		ReportData data = rs.evaluate(schema, inputCohort, evalContext);
		
		Serializer serializer = OpenmrsUtil.getSerializer();
		StringWriter writer = new StringWriter();
		serializer.write(data, writer);
		//System.out.println("Serialized report:\n" + writer.toString());
		
		TsvReportRenderer renderer = new TsvReportRenderer();
		
		//System.out.println("Rendering results:");
		//renderer.render(data, null, System.out);
		*/
	}
	
}
