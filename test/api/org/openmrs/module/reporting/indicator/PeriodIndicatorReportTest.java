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
import org.openmrs.module.reporting.report.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
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
		males.setFemaleIncluded(true);
		
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
		
		ReportService rs = (ReportService) Context.getService(ReportService.class);
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
