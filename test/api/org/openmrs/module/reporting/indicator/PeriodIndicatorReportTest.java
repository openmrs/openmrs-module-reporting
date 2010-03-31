package org.openmrs.module.reporting.indicator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.common.Fraction;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator.IndicatorType;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;


public class PeriodIndicatorReportTest extends BaseModuleContextSensitiveTest {

	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	@Test
	public void shouldEvaluteIndicatorForLocation() throws Exception {
		
		PeriodIndicatorReportDefinition report = new PeriodIndicatorReportDefinition();
		
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setName("Males");
		males.setMaleIncluded(true);
		
		EncounterCohortDefinition atSite = new EncounterCohortDefinition();
		atSite.setName("At Site");
		atSite.addParameter(new Parameter("locationList", "List of Locations", Location.class));
		
		CohortIndicator numberOfMales = new CohortIndicator("Males");
		numberOfMales.addParameter(ReportingConstants.START_DATE_PARAMETER);
		numberOfMales.addParameter(ReportingConstants.END_DATE_PARAMETER);
		numberOfMales.addParameter(ReportingConstants.LOCATION_PARAMETER);
		numberOfMales.setCohortDefinition(males, "");
		numberOfMales.setLocationFilter(atSite, "locationList=${location}");
		report.addIndicator("1.A", "Number of Males", numberOfMales);

		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("location", Context.getLocationService().getLocation(2));
		ReportData data = rs.evaluate(report, context);
		DataSet ds = data.getDataSets().values().iterator().next();
		IndicatorResult ir = (IndicatorResult) ds.iterator().next().getColumnValue("1.A");
		Assert.assertEquals(1, ir.getValue().intValue());
	}
	
	@Test
	public void shouldEvaluteFractionalIndicators() throws Exception {
		
		PeriodIndicatorReportDefinition report = new PeriodIndicatorReportDefinition();
		
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setName("Males");
		males.setMaleIncluded(true);
		
		GenderCohortDefinition all = new GenderCohortDefinition();
		all.setName("All");
		all.setMaleIncluded(true);
		all.setFemaleIncluded(true);
		all.setUnknownGenderIncluded(true);
		
		EncounterCohortDefinition atSite = new EncounterCohortDefinition();
		atSite.setName("At Site");
		atSite.addParameter(new Parameter("locationList", "List of Locations", Location.class));
		
		CohortIndicator percentMales = new CohortIndicator("Males");
		percentMales.setType(IndicatorType.FRACTION);
		percentMales.addParameter(ReportingConstants.START_DATE_PARAMETER);
		percentMales.addParameter(ReportingConstants.END_DATE_PARAMETER);
		percentMales.addParameter(ReportingConstants.LOCATION_PARAMETER);
		percentMales.setCohortDefinition(males, "");
		percentMales.setDenominator(all, "");
		percentMales.setLocationFilter(atSite, "locationList=${location}");
		report.addIndicator("1.A", "Percent of Males", percentMales);
		
		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		EvaluationContext context = new EvaluationContext();
		context.addParameterValue("location", Context.getLocationService().getLocation(2));
		ReportData data = rs.evaluate(report, context);
		DataSet ds = data.getDataSets().values().iterator().next();
		IndicatorResult ir = (IndicatorResult) ds.iterator().next().getColumnValue("1.A");
		Fraction fraction = (Fraction) ir.getValue();
		Assert.assertEquals(1, fraction.getNumerator());
		Assert.assertEquals(6, fraction.getDenominator());
	}
}
