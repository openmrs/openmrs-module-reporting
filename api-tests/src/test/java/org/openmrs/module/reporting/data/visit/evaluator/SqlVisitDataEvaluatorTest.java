/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.visit.evaluator;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.SqlEncounterDataDefinition;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.definition.SqlVisitDataDefinition;
import org.openmrs.module.reporting.data.visit.service.VisitDataService;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.context.VisitEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;
import org.openmrs.module.reporting.query.visit.VisitIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class SqlVisitDataEvaluatorTest extends BaseModuleContextSensitiveTest {

	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";

	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	@Autowired
	private VisitDataService visitDataService;

	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link org.openmrs.test.BaseContextSensitiveTest} is run right before this method.
	 *
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	@Test
	public void testEvaluate() throws Exception {
		String sql = "select v.visit_id, v.uuid from visit v where v.visit_id in (:visitIds)";
		SqlVisitDataDefinition definition = new SqlVisitDataDefinition();
		definition.setSql(sql);

		VisitEvaluationContext context = new VisitEvaluationContext();
		context.setBaseVisits(new VisitIdSet(3, 4));
		EvaluatedVisitData data = visitDataService.evaluate(definition, context);

		assertThat(data.getData().size(), is(2));
		assertThat((String) data.getData().get(3), is("6f85b2a6-6b78-11e0-93c3-18a905e044dc"));
		assertThat((String) data.getData().get(4), is("7d8c1980-6b78-11e0-93c3-18a905e044dc"));
	}

}