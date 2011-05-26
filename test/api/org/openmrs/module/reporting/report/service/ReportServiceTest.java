package org.openmrs.module.reporting.report.service;


import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Priority;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.renderer.TsvReportRenderer;
import org.openmrs.module.reporting.web.renderers.IndicatorReportWebRenderer;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class ReportServiceTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void shouldSaveReportDefinition() throws Exception { 		
		ReportDefinitionService service = Context.getService(ReportDefinitionService.class);
		ReportDefinition reportDefinition = new ReportDefinition();		
		reportDefinition.setName("Testing");
		ReportDefinition savedReportDefinition = service.saveDefinition(reportDefinition);		
		Assert.assertTrue(savedReportDefinition.getId()!=null);
	}

	/**
	 * @see {@link ReportService#runReport(ReportRequest)}
	 * 
	 */
	@Test
	@Verifies(value = "should set uuid on the request", method = "runReport(ReportRequest)")
	public void runReport_shouldSetUuidOnTheRequest() throws Exception {
		ReportDefinition def = new ReportDefinition();
		ReportRequest request = new ReportRequest(new Mapped<ReportDefinition>(def, null), null, null, Priority.NORMAL);
		Context.getService(ReportService.class).runReport(request);
		Assert.assertNotNull(request.getUuid());
	}
	
	/**
	 * @see {@link ReportService#runReport(ReportRequest)}
	 * 
	 */
	@Test
	@Verifies(value = "should render the report if a plain renderer is specified", method = "runReport(ReportRequest)")
	public void runReport_shouldRenderTheReportIfAPlainRendererIsSpecified() throws Exception {
		ReportDefinition def = new ReportDefinition();
		SqlDataSetDefinition dsd = new SqlDataSetDefinition();
		dsd.setSqlQuery("select count(*) from patient");
		def.addDataSetDefinition("patients", dsd, null);
		ReportRenderer renderer = new TsvReportRenderer();
		ReportRequest request = new ReportRequest(new Mapped<ReportDefinition>(def, null), null, new RenderingMode(renderer, "TSV", null, 100), Priority.NORMAL);
		Report result = Context.getService(ReportService.class).runReport(request);
		Assert.assertNotNull(result.getReportData());
		Assert.assertNotNull(result.getRenderedOutput());
	}
	
	/**
	 * @see {@link ReportService#runReport(ReportRequest)}
	 * 
	 */
	@Test
	@Verifies(value = "should not render the report if a web renderer is specified", method = "runReport(ReportRequest)")
	public void runReport_shouldNotRenderTheReportIfAWebRendererIsSpecified() throws Exception {
		ReportDefinition def = new ReportDefinition();
		WebReportRenderer renderer = new IndicatorReportWebRenderer(); 
		ReportRequest request = new ReportRequest(new Mapped<ReportDefinition>(def, null), null, new RenderingMode(renderer, "Web", null, 100), Priority.NORMAL);
		Report result = Context.getService(ReportService.class).runReport(request);
		Assert.assertNotNull(result.getReportData());
		Assert.assertNull(result.getRenderedOutput());
	}
}