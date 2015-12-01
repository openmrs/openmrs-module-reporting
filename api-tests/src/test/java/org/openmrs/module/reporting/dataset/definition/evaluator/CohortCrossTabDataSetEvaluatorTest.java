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

import java.util.Date;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests the evaluation of a CohortDataSetEvaluator
 */
public class CohortCrossTabDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
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
	 * @see {@link CohortDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate a CohortCrossTabDataSetDefinition", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateACohortIndicatorDataSetDefinition() throws Exception {
		
		AgeCohortDefinition childrenOnDate = new AgeCohortDefinition();
		childrenOnDate.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		childrenOnDate.setMaxAge(14);
		
		AgeCohortDefinition adultsOnDate = new AgeCohortDefinition();
		adultsOnDate.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
		adultsOnDate.setMinAge(15);
		
		AgeCohortDefinition unknownAge = new AgeCohortDefinition();
		unknownAge.setUnknownAgeIncluded(true);
		
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setMaleIncluded(true);
		
		GenderCohortDefinition females = new GenderCohortDefinition();
		females.setFemaleIncluded(true);
		
		GenderCohortDefinition unknownGender = new GenderCohortDefinition();
		unknownGender.setUnknownGenderIncluded(true);
		
		CohortCrossTabDataSetDefinition d = new CohortCrossTabDataSetDefinition();
		d.addParameter(ReportingConstants.END_DATE_PARAMETER);
		
		d.addRow("male", new Mapped<CohortDefinition>(males, null));
		d.addRow("female", new Mapped<CohortDefinition>(females, null));
		d.addRow("unknown", new Mapped<CohortDefinition>(unknownGender, null));
		
		d.addColumn("adult", new Mapped<CohortDefinition>(adultsOnDate, ParameterizableUtil.createParameterMappings("effectiveDate=${endDate}")));
		d.addColumn("child", new Mapped<CohortDefinition>(childrenOnDate, ParameterizableUtil.createParameterMappings("effectiveDate=${endDate}")));
		d.addColumn("unknown", new Mapped<CohortDefinition>(unknownAge, null));
		
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue(ReportingConstants.END_DATE_PARAMETER.getName(), DateUtil.getDateTime(2000, 1, 1));
		
		ReportDefinition report = new ReportDefinition();
		report.addParameter(ReportingConstants.END_DATE_PARAMETER);
		report.addDataSetDefinition(d, ParameterizableUtil.createParameterMappings("endDate=${endDate}"));
		
		ReportData results = Context.getService(ReportDefinitionService.class).evaluate(report, context);
		MapDataSet ds = (MapDataSet)results.getDataSets().values().iterator().next();
		
		Assert.assertEquals(2, ((Cohort)ds.getData(ds.getMetaData().getColumn("male.adult"))).size());
		Assert.assertEquals(0, ((Cohort)ds.getData(ds.getMetaData().getColumn("male.child"))).size());
		Assert.assertEquals(3, ((Cohort)ds.getData(ds.getMetaData().getColumn("male.unknown"))).size());
		Assert.assertEquals(2, ((Cohort)ds.getData(ds.getMetaData().getColumn("female.adult"))).size());
		Assert.assertEquals(1, ((Cohort)ds.getData(ds.getMetaData().getColumn("female.child"))).size());
		Assert.assertEquals(5, ((Cohort)ds.getData(ds.getMetaData().getColumn("female.unknown"))).size());
		Assert.assertEquals(0, ((Cohort)ds.getData(ds.getMetaData().getColumn("unknown.adult"))).size());
		Assert.assertEquals(0, ((Cohort)ds.getData(ds.getMetaData().getColumn("unknown.child"))).size());
		Assert.assertEquals(1, ((Cohort)ds.getData(ds.getMetaData().getColumn("unknown.unknown"))).size());
	}
	
}
