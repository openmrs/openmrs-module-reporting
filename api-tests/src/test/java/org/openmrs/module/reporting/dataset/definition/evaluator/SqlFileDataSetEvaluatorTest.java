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

import org.hibernate.cfg.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlFileDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Properties;

@SkipBaseSetup
public class SqlFileDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	@Autowired
    PersonService personService;
	
	@Before
	public void setup() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
        authenticate();
        getConnection().commit();
	}

    @Override
    public Properties getRuntimeProperties() {
        Properties p = super.getRuntimeProperties();
        p.put("connection.url", p.getProperty(Environment.URL));
        p.put(Environment.URL, p.getProperty(Environment.URL) + ";MVCC=TRUE");
        p.put("connection.driver_class", p.getProperty(Environment.DRIVER));
        return p;
    }

    /**
	 * @see {@link SqlDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	public void evaluate_shouldEvaluateSqlResource() throws Exception {
		SqlFileDataSetDefinition d = new SqlFileDataSetDefinition();
		d.setSqlResource("org/openmrs/module/reporting/dataset/definition/evaluator/sqlFileNoParams.sql");
		SimpleDataSet result = (SimpleDataSet) Context.getService(DataSetDefinitionService.class).evaluate(d, null);
		Assert.assertEquals(1, result.getRows().size());
		Assert.assertEquals(3, result.getMetaData().getColumnCount());
		DataSetRow firstRow = result.getRows().get(0);
		Assert.assertEquals(2, firstRow.getColumnValue("patient_id"));
		Assert.assertEquals("M", firstRow.getColumnValue("gender"));
		Assert.assertEquals(DateUtil.getDateTime(1975, 4, 8), firstRow.getColumnValue("birthdate"));
	}

    /**
     * @see {@link SqlDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
     */
    @Test
    public void evaluate_shouldEvaluateSqlResourceWithParams() throws Exception {
        SqlFileDataSetDefinition d = new SqlFileDataSetDefinition();
        d.setSqlResource("org/openmrs/module/reporting/dataset/definition/evaluator/sqlFileWithParams.sql");
        d.addParameter(new Parameter("birthplace", "birthplace", PersonAttributeType.class));

        EvaluationContext context = new EvaluationContext();
        context.addParameterValue("birthplace", personService.getPersonAttributeTypeByName("Birthplace"));

        SimpleDataSet result = (SimpleDataSet) Context.getService(DataSetDefinitionService.class).evaluate(d, context);
        Assert.assertEquals(4, result.getRows().size());
        Assert.assertEquals(4, result.getMetaData().getColumnCount());
        DataSetRow firstRow = result.getRows().get(0);
        Assert.assertEquals(2, firstRow.getColumnValue("patient_id"));
        Assert.assertEquals("Mooresville, NC", firstRow.getColumnValue("birthplace"));
    }
}
