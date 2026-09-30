/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.OptionalParameterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.library.BuiltInCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the OptionalParameterCohortDefinition
 */
public class OptionalParameterCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected final Log log = LogFactory.getLog(getClass());
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	@Autowired
	CohortDefinitionService cohortDefinitionService;

	@Autowired
	BuiltInCohortDefinitionLibrary builtInCohortDefinitionLibrary;
	
	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	/**
	 * @see {@link OptionalParameterCohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)}
	 */
	@Test
	public void evaluate_shouldSupportIntegerParameter() throws Exception {

		Cohort allPatients = cohortDefinitionService.evaluate(new AllPatientsCohortDefinition(), new EvaluationContext());
		Cohort males = cohortDefinitionService.evaluate(builtInCohortDefinitionLibrary.getMales(), new EvaluationContext());

		GenderCohortDefinition gender = new GenderCohortDefinition();
		gender.addParameter(new Parameter("maleIncluded", "Males", Boolean.class));
		gender.addParameter(new Parameter("femaleIncluded", "Females", Boolean.class));

		OptionalParameterCohortDefinition cd = new OptionalParameterCohortDefinition(gender, "maleIncluded", "femaleIncluded");
		
		EvaluationContext context = new EvaluationContext();

		context.addParameterValue("maleIncluded", Boolean.TRUE);
		Cohort test1 = cohortDefinitionService.evaluate(cd, context);
		Assert.assertEquals(allPatients.getSize(), test1.getSize());

		context.addParameterValue("femaleIncluded", Boolean.FALSE);
		Cohort test2 = cohortDefinitionService.evaluate(cd, context);
		Assert.assertEquals(males.getSize(), test2.getSize());
	}
}
