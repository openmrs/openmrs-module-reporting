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
package org.openmrs.module.reporting.report.renderer;

import java.io.InputStream;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.util.IndicatorUtil;
import org.openmrs.module.reporting.report.PeriodIndicatorReportUtil;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsClassLoader;

/**
 * Supports rendering a series of Cohorts with particular datasets
 */
public class CohortDetailReportRendererTest extends BaseModuleContextSensitiveTest {

	@Test
	public void shouldRenderIndicatorsWithDifferentDatasets() throws Exception {
		
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setName("Males");
		males.setMaleIncluded(true);
		
		GenderCohortDefinition females = new GenderCohortDefinition();
		females.setName("Females");
		females.setFemaleIncluded(true);
		
		CohortIndicator numberOfMales = CohortIndicator.newCountIndicator("Males", new Mapped<CohortDefinition>(males, null), null);
		numberOfMales.setParameters(IndicatorUtil.getDefaultParameters());
		CohortIndicator numberOfFemales = CohortIndicator.newCountIndicator("Females", new Mapped<CohortDefinition>(females, null), null);
		numberOfFemales.setParameters(IndicatorUtil.getDefaultParameters());
		
		PeriodIndicatorReportDefinition report = new PeriodIndicatorReportDefinition();
		report.setName("Test Report");
		PeriodIndicatorReportUtil.ensureDataSetDefinition(report);
		
		report.addIndicator("1", "Number of Males", numberOfMales);
		report.addIndicator("2", "Number of Females", numberOfFemales);
		
		final ReportDesign design = new ReportDesign();
		design.setName("TestDesign");
		design.setReportDefinition(report);
		design.setRendererType(CohortDetailReportRenderer.class);
		
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("designFile");
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream("org/openmrs/module/reporting/report/renderer/CohortDetailReportRendererResource.xml");
		resource.setContents(IOUtils.toByteArray(is));
		design.addResource(resource);

		CohortDetailReportRenderer renderer = new CohortDetailReportRenderer() {
			public ReportDesign getDesign(String argument) {
				return design;
			}
		};
		
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("startDate", new Date());
		context.addParameterValue("endDate", new Date());
		context.addParameterValue("location", Context.getLocationService().getLocation(2));

		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		ReportData data = rs.evaluate(report, context);
		renderer.render(data, "xxx:yyy", System.out);
	}

}
