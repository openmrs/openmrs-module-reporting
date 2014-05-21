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
package org.openmrs.module.reporting.data.patient.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PersonAttributeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.definition.library.AllDefinitionLibraries;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Test the PatientDataServiceImpl
 */
public class PatientDataServiceImplTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
    public static final String TEST_PATIENT_ATTR_TYPE_UUID = "test-patient-attr-type-uuid";

    @Autowired
    private AllDefinitionLibraries libraries;
	
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
	 * @see PatientDataServiceImpl#evaluate(org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @verifies evaluate a patient query
	 */
	@Test
	public void evaluate_shouldEvaluateAnPatientData() throws Exception {
		PatientDataDefinition definition = new PatientIdDataDefinition();
		PatientData data = Context.getService(PatientDataService.class).evaluate(definition, new EvaluationContext());
		Assert.assertNotNull(data);
	}
	
	/**
	 * @see PatientDataServiceImpl#saveDefinition(org.openmrs.module.reporting.evaluation.Definition)
	 * @verifies save a patient query
	 */
	@Test
	public void saveDefinition_shouldSaveAnPatientData() throws Exception {
		PatientDataDefinition definition = new PatientIdDataDefinition();
		definition.setName("All Patient Ids");
		definition = Context.getService(PatientDataService.class).saveDefinition(definition);
		Assert.assertNotNull(definition.getId());
		Assert.assertNotNull(definition.getUuid());
		PatientDataDefinition loadedDefinition = Context.getService(PatientDataService.class).getDefinitionByUuid(definition.getUuid());
		Assert.assertEquals(definition, loadedDefinition);
	}

	/**
	 * @see PatientDataServiceImpl#evaluate(org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @verifies evaluate a patient query
	 */
	@Test
	public void evaluate_shouldPerformABatchedEvaluation() throws Exception {
		TestUtil.updateGlobalProperty("reporting.dataEvaluationBatchSize", "1");
		PatientDataDefinition definition = new PatientIdDataDefinition();
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2,6,7,8"));

		PatientData data = Context.getService(PatientDataService.class).evaluate(definition, context);
		TestUtil.assertCollectionsEqual(context.getBaseCohort().getMemberIds(), data.getData().values());
	}

    @Test
    public void evaluate_shouldRemoveTestPatientsFromExistingBaseCohort() throws Exception {
        // mark a couple patients as test patients
        PersonAttributeType testAttributeType = setUpTestPatientPersonAttribute(2, 7);
        CohortDefinition testPatientCohortDefinition = setUpTestPatientCohortDefinition(testAttributeType);
        TestUtil.updateGlobalProperty(ReportingConstants.GLOBAL_PROPERTY_TEST_PATIENTS_COHORT_DEFINITION, testPatientCohortDefinition.getUuid());

        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort("2,6,7,8"));

        PatientData data = Context.getService(PatientDataService.class).evaluate(new PatientIdDataDefinition(), context);
        assertThat(data.getData().get(2), nullValue());
        assertThat((Integer) data.getData().get(6), is(6));
        assertThat(data.getData().get(7), nullValue());
        assertThat((Integer) data.getData().get(8), is(8));
    }

    @Test
    public void evaluate_shouldRemoveTestPatientsWhenNoBaseCohortSpecified() throws Exception {
        // mark a couple patients as test patients
        PersonAttributeType testAttributeType = setUpTestPatientPersonAttribute(2, 7);
        CohortDefinition testPatientCohortDefinition = setUpTestPatientCohortDefinition(testAttributeType);
        TestUtil.updateGlobalProperty(ReportingConstants.GLOBAL_PROPERTY_TEST_PATIENTS_COHORT_DEFINITION, testPatientCohortDefinition.getUuid());

        EvaluationContext context = new EvaluationContext();

        PatientData data = Context.getService(PatientDataService.class).evaluate(new PatientIdDataDefinition(), context);
        assertThat(data.getData().get(2), nullValue());
        assertThat(data.getData().get(7), nullValue());
    }

    @Test
    public void evaluate_shouldRemoveTestPatientsUsingLibraryDefinition() throws Exception {
        // mark a couple patients as test patients
        PersonAttributeType testAttributeType = setUpTestPatientPersonAttribute(2, 7);
        TestPatientCohortDefinitionLibrary library = new TestPatientCohortDefinitionLibrary();

        libraries.addLibrary(library);
        TestUtil.updateGlobalProperty(ReportingConstants.GLOBAL_PROPERTY_TEST_PATIENTS_COHORT_DEFINITION,
                "library:patientDataServiceImplTest.testPatients");

        EvaluationContext context = new EvaluationContext();

        PatientData data = Context.getService(PatientDataService.class).evaluate(new PatientIdDataDefinition(), context);
        assertThat(data.getData().get(2), nullValue());
        assertThat(data.getData().get(7), nullValue());

        libraries.removeLibrary(library);
    }

    private CohortDefinition setUpTestPatientCohortDefinition(PersonAttributeType testAttributeType) {
        PersonAttributeCohortDefinition cohortDefinition = new PersonAttributeCohortDefinition();
        cohortDefinition.setName("Test Patients");
        cohortDefinition.setAttributeType(testAttributeType);
        cohortDefinition.setValues(Arrays.asList("true"));
        Context.getService(CohortDefinitionService.class).saveDefinition(cohortDefinition);
        return cohortDefinition;
    }

    private PersonAttributeType setUpTestPatientPersonAttribute(Integer... testPatientIds) {
        PersonAttributeType pat = new PersonAttributeType();
        pat.setName("Test Patient");
        pat.setDescription("Not a real patient");
        pat.setFormat("java.lang.Boolean");
        pat.setUuid(TEST_PATIENT_ATTR_TYPE_UUID);

        Context.getPersonService().savePersonAttributeType(pat);

        PatientService patientService = Context.getPatientService();
        for (Integer patientId : testPatientIds) {
            Patient patient = patientService.getPatient(patientId);
            patient.addAttribute(new PersonAttribute(pat, "true"));
            patientService.savePatient(patient);
        }

        return pat;
    }

    public class TestPatientCohortDefinitionLibrary extends BaseDefinitionLibrary<CohortDefinition> {

        @Override
        public Class<? super CohortDefinition> getDefinitionType() {
            return CohortDefinition.class;
        }

        @Override
        public String getKeyPrefix() {
            return "patientDataServiceImplTest.";
        }

        @DocumentedDefinition("testPatients")
        public CohortDefinition getTestPatients() {
            PersonAttributeCohortDefinition cohortDefinition = new PersonAttributeCohortDefinition();
            cohortDefinition.setAttributeType(Context.getPersonService().getPersonAttributeTypeByUuid(TEST_PATIENT_ATTR_TYPE_UUID));
            cohortDefinition.setValues(Arrays.asList("true"));
            return cohortDefinition;
        }

    }

}