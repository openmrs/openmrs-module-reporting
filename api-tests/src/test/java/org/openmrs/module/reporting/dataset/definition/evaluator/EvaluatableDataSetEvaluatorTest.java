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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import java.util.Iterator;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EvaluatableDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class EvaluatableDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {

	@Autowired
	DataSetDefinitionService service;

	@Test
	public void evaluate() throws Exception {
		EvaluatableDataSetDefinition dsd = new AnEvaluatableDataSetDefinition();
		DataSet dataSet = service.evaluate(dsd, new EvaluationContext());
		assertThat(dataSet.getDefinition(), Is.<DataSetDefinition>is(dsd));

		Iterator<DataSetRow> iter = dataSet.iterator();
		DataSetRow row = iter.next();
		assertThat(row.getColumnValue("one"), Is.<Object>is(1));
		assertFalse(iter.hasNext());
	}
	
}