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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EvaluatableCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class EvaluatableCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	@Autowired
	CohortDefinitionService service;

	@Test
	public void evaluate() throws Exception {
		EvaluatableCohortDefinition evaluatableCohortDefinition = new AnEvaluatableCohortDefinition();
		EvaluatedCohort cohort = service.evaluate(evaluatableCohortDefinition, new EvaluationContext());
		assertThat(cohort.size(), is(1));
		assertThat(cohort.getDefinition(), Is.<CohortDefinition>is(evaluatableCohortDefinition));
	}
	
}