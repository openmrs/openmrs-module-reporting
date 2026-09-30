/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
import org.openmrs.module.reporting.common.ObjectUtil;
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
		
		//There are two weight concept names for locale en { WT, WEIGHT (KG) } in standardTestDataset.xml
		//With java 8, the order of these names in the names collection changes, hence
		//leading us to get any of the two. Therefore, by not hard coding either of the two names,
		//we ensure that we test with whichever name was returned as first in the collection.
		String columnName = ObjectUtil.format(wt).replaceAll("\\s", "_").replaceAll("-", "_").toUpperCase();
		Assert.assertEquals(Double.valueOf(77), row.getColumnValue(columnName));
		
		Assert.assertEquals("SINGLE", row.getColumnValue("CIVIL_STATUS"));
	}
}