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
package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.contrib.testdata.builder.EncounterBuilder;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.EncounterAndObsDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the evaluation of an EncounterAndObsDataSetEvaluator
 */
public class EncounterAndObsDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private DataSetDefinitionService dataSetDefinitionService;

	@Autowired
	private TestDataManager data;

	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/" + new TestUtil().getTestDatasetFilename("ReportTestDataset"));
	}

	/**
	 * @return base data set definition for use with testing
	 */
	protected EncounterAndObsDataSetDefinition createEncounterAndObsDataSetDefinition() {
		EncounterAndObsDataSetDefinition dsd = new EncounterAndObsDataSetDefinition();
		BasicEncounterQuery q = new BasicEncounterQuery();
		q.addForm(data.getFormService().getForm(2));
		q.addEncounterType(data.getEncounterService().getEncounterType(6));
		dsd.addRowFilter(Mapped.noMappings(q));
		return dsd;
	}

	@Test
	@Verifies(value = "should contain all obs values for each encounter", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldContainAllObsValuesForEachEncounter() throws Exception {

		Patient p = data.randomPatient().save();
		EncounterBuilder eb = data.randomEncounter().patient(p).encounterType(6).form(2);

		Concept wt = data.getConceptService().getConcept(5089);
		Concept civilStatus = data.getConceptService().getConcept(4);
		Concept single = data.getConceptService().getConcept(5);

		eb.obs(wt, 77);
		eb.obs(civilStatus, single);

		Encounter e = eb.save();

		SimpleDataSet result = (SimpleDataSet)dataSetDefinitionService.evaluate(createEncounterAndObsDataSetDefinition(), null);
		Assert.assertEquals(1, result.getRows().size());
		DataSetRow row = result.getRows().get(0);
		Assert.assertEquals(e.getEncounterId(), row.getColumnValue("ENCOUNTER_ID"));
		Assert.assertEquals(p.getPatientId(), row.getColumnValue("PATIENT_ID"));
		Assert.assertEquals(e.getEncounterType().getName(), row.getColumnValue("ENCOUNTER_TYPE"));
		Assert.assertEquals(e.getEncounterDatetime(), row.getColumnValue("ENCOUNTER_DATETIME"));
		Assert.assertEquals(p.getPatientId(), row.getColumnValue("PATIENT_ID"));
		Assert.assertEquals(e.getLocation().getName(), row.getColumnValue("LOCATION"));
		Assert.assertEquals(Double.valueOf(77), row.getColumnValue("WT"));
		Assert.assertEquals("SINGLE", row.getColumnValue("CIVIL_STATUS"));
	}
}