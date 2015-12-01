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
package org.openmrs.module.reporting.data.patient.evaluator;

import java.io.InputStream;

import org.junit.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ScriptingLanguage;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ScriptedCompositionPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsClassLoader;

/**
 * Test of ScriptedCompositionPatientDataDefinitionEvaluator
 */
public class ScriptedCompositionPatientDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	
	/**
	 * @see ScriptedCompositionPatientDataDefinitionEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return the number of days since last encounter of the specified types from a
	 *           specified date parameter
	 */
	@Test
	public void evaluate_shouldReturnNumberOfDaysSinceLastEncounterOfTheSpecifiedTypes() throws Exception {
		
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream(
		    "org/openmrs/module/reporting/report/script/GroovyBasedDaysSinceLastVisitCalculation.txt");
		String script = new String(IOUtils.toByteArray(is), "UTF-8");
		IOUtils.closeQuietly(is);
		
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("22"));
		context.addParameterValue("date", DateUtil.getDateTime(2009, 10, 21));
		
		EncountersForPatientDataDefinition lastEncounter = new EncountersForPatientDataDefinition();
		lastEncounter.setWhich(TimeQualifier.LAST);
		lastEncounter.addType(Context.getEncounterService().getEncounterType(6));
		
		ScriptedCompositionPatientDataDefinition daysSinceLastVisit = new ScriptedCompositionPatientDataDefinition();
		daysSinceLastVisit.setScriptType(new ScriptingLanguage("groovy"));
		daysSinceLastVisit.setScriptCode(script);
		daysSinceLastVisit.getContainedDataDefinitions().put("patientLastVisit",
		    new Mapped<PatientDataDefinition>(lastEncounter, null));
		
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(daysSinceLastVisit, context);
		Assert.assertEquals("2 days", pd.getData().get(22));
	}
	
	/**
	 * @see ScriptedCompositionPatientDataDefinitionEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return a specified alert based on patient's last weight value
	 */
	@Test
	public void evaluate_shouldReturnSpecifiedAlertBasedOnLastWeightValue() throws Exception {
		
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream(
		    "org/openmrs/module/reporting/report/script/GroovyBasedCustomAlertBasedOnLastWeightValue.txt");
		String script = new String(IOUtils.toByteArray(is), "UTF-8");
		IOUtils.closeQuietly(is);
		
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("7,20,21,22"));
		
		ObsForPersonDataDefinition lastWeight = new ObsForPersonDataDefinition();
		lastWeight.setWhich(TimeQualifier.LAST);
		lastWeight.setQuestion(Context.getConceptService().getConcept(5089));
		
		ScriptedCompositionPatientDataDefinition daysSinceLastVisit = new ScriptedCompositionPatientDataDefinition();
		daysSinceLastVisit.setScriptType(new ScriptingLanguage("groovy"));
		daysSinceLastVisit.setScriptCode(script);
		daysSinceLastVisit.getContainedDataDefinitions().put("lastWeight",
		    new Mapped<PatientDataDefinition>(new PersonToPatientDataDefinition(lastWeight), null));
		EvaluatedPatientData daysSinceLastVisitResult = Context.getService(PatientDataService.class).evaluate(
		    daysSinceLastVisit, context);
		
		Assert.assertEquals("Normal", daysSinceLastVisitResult.getData().get(7));
		Assert.assertEquals("The recorded weight value might be incorrect!", daysSinceLastVisitResult.getData().get(20));
		Assert.assertEquals("High", daysSinceLastVisitResult.getData().get(21));
		Assert.assertEquals("The recorded weight value might be incorrect!", daysSinceLastVisitResult.getData().get(22));
	}
}
