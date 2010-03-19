package org.openmrs.module.reporting.indicator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.PatientSetService.PatientLocationMethod;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.LocationCohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator.IndicatorType;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;


public class PeriodIndicatorReportTest extends BaseModuleContextSensitiveTest {

	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

	@Before
	public void runBeforeEachTest() throws Exception {
		authenticate();
	}
	
	@Test
	public void shouldEvaluteIndicatorForLocation() throws Exception {
		
		PeriodIndicatorReportDefinition report = new PeriodIndicatorReportDefinition();
		
		GenderCohortDefinition males = new GenderCohortDefinition();
		males.setName("Males");
		males.setMaleIncluded(true);
		
		LocationCohortDefinition atSite = new LocationCohortDefinition();
		atSite.setName("At Site");
		atSite.setCalculationMethod(PatientLocationMethod.ANY_ENCOUNTER);
		atSite.addParameter(new Parameter("locations", "List of Locations", Location.class));
		
		CohortIndicator numberOfMales = new CohortIndicator("Males");
		numberOfMales.addParameter(ReportingConstants.START_DATE_PARAMETER);
		numberOfMales.addParameter(ReportingConstants.END_DATE_PARAMETER);
		numberOfMales.addParameter(ReportingConstants.LOCATION_PARAMETER);
		numberOfMales.setCohortDefinition(males, "");
		numberOfMales.setLocationFilter(atSite, "locations=${location}");
		report.addIndicator("1.A", "Number of Males", numberOfMales);
		
		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		for (Location l : Context.getLocationService().getAllLocations()) {
			EvaluationContext context = new EvaluationContext();
			context.addParameterValue("location", l);
			ReportData data = rs.evaluate(report, context);
			CsvReportRenderer renderer = new CsvReportRenderer();
			System.out.println("Location: " + l.getName());
			renderer.render(data, null, System.out);
			System.out.println("---------------");
		}
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
		
		LocationCohortDefinition atSite = new LocationCohortDefinition();
		atSite.setName("At Site");
		atSite.setCalculationMethod(PatientLocationMethod.ANY_ENCOUNTER);
		atSite.addParameter(new Parameter("locations", "List of Locations", Location.class));
		
		CohortIndicator percentMales = new CohortIndicator("Males");
		percentMales.setType(IndicatorType.FRACTION);
		percentMales.addParameter(ReportingConstants.START_DATE_PARAMETER);
		percentMales.addParameter(ReportingConstants.END_DATE_PARAMETER);
		percentMales.addParameter(ReportingConstants.LOCATION_PARAMETER);
		percentMales.setCohortDefinition(males, "");
		percentMales.setDenominator(all, "");
		percentMales.setLocationFilter(atSite, "locations=${location}");
		report.addIndicator("1.A", "Percent of Males", percentMales);
		
		ReportDefinitionService rs = Context.getService(ReportDefinitionService.class);
		for (Location l : Context.getLocationService().getAllLocations()) {
			EvaluationContext context = new EvaluationContext();
			context.addParameterValue("location", l);
			ReportData data = rs.evaluate(report, context);
			CsvReportRenderer renderer = new CsvReportRenderer();
			System.out.println("Location: " + l.getName());
			renderer.render(data, null, System.out);
			System.out.println("---------------");
		}
	}
}
