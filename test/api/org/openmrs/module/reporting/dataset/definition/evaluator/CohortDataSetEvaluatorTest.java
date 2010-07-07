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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.definition.CohortDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests the evaluation of a CohortDataSetEvaluator
 */
public class CohortDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	/**
	 * @see {@link CohortDataSetEvaluator#evaluate(DataSetDefinition,EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate a CohortDataSetDefinition", method = "evaluate(DataSetDefinition,EvaluationContext)")
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
		
		CohortDataSetDefinition d = new CohortDataSetDefinition();
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
		
		CsvReportRenderer renderer = new CsvReportRenderer();
		renderer.render(results, null, System.out);
	}
	
}
