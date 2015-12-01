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

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimplePatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.util.IndicatorUtil;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.util.PeriodIndicatorReportUtil;
import org.openmrs.module.reporting.serializer.ReportingSerializer;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Supports rendering a series of Cohorts with particular datasets
 */
public class CohortDetailReportRendererTest extends BaseModuleContextSensitiveTest {

	@Test
	public void shouldRenderIndicatorsWithDifferentDatasets() throws Exception {
		
		// We first set up a report with 2 indicators, numbered 1 and 2
		
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
		
		// Then, we construct a Map from indicator number to DataSetDefinition we wish to use to view it's underlying Cohort
		
		Map<String, Mapped<? extends DataSetDefinition>> m = new HashMap<String, Mapped<? extends DataSetDefinition>>();
		
		SimplePatientDataSetDefinition maleView = new SimplePatientDataSetDefinition();
		maleView.addPatientProperty("patientId");
		maleView.addIdentifierType(Context.getPatientService().getPatientIdentifierType(1));
		maleView.addIdentifierType(Context.getPatientService().getPatientIdentifierType(2));
		m.put("1", new Mapped<DataSetDefinition>(maleView, null));
		
		SimplePatientDataSetDefinition femaleView = new SimplePatientDataSetDefinition();
		femaleView.addPatientProperty("patientId");
		femaleView.addPatientProperty("age");
		femaleView.addPatientProperty("gender");
		m.put("2", new Mapped<DataSetDefinition>(femaleView, null));
		
		// Next, we set up the ReportDesign and ReportDesignResource files for the renderer
		
		ReportingSerializer serializer = new ReportingSerializer();
		String designXml = serializer.serialize(m);
		
		final ReportDesign design = new ReportDesign();
		design.setName("TestDesign");
		design.setReportDefinition(report);
		design.setRendererType(CohortDetailReportRenderer.class);
		
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("designFile");  // Note: You must name your resource exactly like this for it to work
		resource.setContents(designXml.getBytes());
		design.addResource(resource);

		// For now, we need this little magic to simulate what would happen if this were all stored in the database via the UI
		
		CohortDetailReportRenderer renderer = new CohortDetailReportRenderer() {
			public ReportDesign getDesign(String argument) {
				return design;
			}
		};
		
		// We construct an EvaluationContext (in this case the parameters aren't used, but included here for reference)
		
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("startDate", new Date());
		context.addParameterValue("endDate", new Date());
		context.addParameterValue("location", Context.getLocationService().getLocation(2));

		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		ReportData data = rs.evaluate(report, context);
		

		// We demonstrate here how we can use this renderer to output to HTML
		String outFile = System.getProperty("java.io.tmpdir") + File.separator + "test.html";
		FileOutputStream fos = new FileOutputStream(outFile); 
		renderer.render(data, "xxx:html", fos);
		fos.close();
		
		// We demonstrate here how we can use this renderer to output to Excel
		outFile = System.getProperty("java.io.tmpdir") + File.separator + "cohortDetailReportRendererTest.xls";
		fos = new FileOutputStream(outFile); 
		renderer.render(data, "xxx:xls", fos);
		fos.close();
	}

}