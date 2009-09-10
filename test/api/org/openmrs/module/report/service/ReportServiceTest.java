package org.openmrs.module.report.service;


import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.definition.DataSetDefinition;
import org.openmrs.module.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.report.Report;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.ReportRequest;
import org.openmrs.module.report.renderer.RenderingMode;
import org.openmrs.module.report.renderer.ReportRenderer;
import org.openmrs.module.report.renderer.TsvReportRenderer;
import org.openmrs.module.reporting.web.renderers.IndicatorReportWebRenderer;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class ReportServiceTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void shouldSaveReportDefinition() throws Exception { 		
		ReportService service = Context.getService(ReportService.class);
		ReportDefinition reportDefinition = new ReportDefinition();		
		reportDefinition.setName("Testing");
		ReportDefinition savedReportDefinition = service.saveReportDefinition(reportDefinition);		
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
		ReportRequest request = new ReportRequest(def, null, null, null, null);
		Assert.assertNull(request.getUuid());
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
		DataSetDefinition dsd = new PatientDataSetDefinition();
		def.addDataSetDefinition("patients", dsd, null);
		ReportRenderer renderer = new TsvReportRenderer();
		ReportRequest request = new ReportRequest(def, null, null, new RenderingMode(renderer, "TSV", null, 100), null);
		Report result = Context.getService(ReportService.class).runReport(request);
		Assert.assertNotNull(result.getRawData());
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
		ReportRequest request = new ReportRequest(def, null, null, new RenderingMode(renderer, "Web", null, 100), null);
		Report result = Context.getService(ReportService.class).runReport(request);
		Assert.assertNotNull(result.getRawData());
		Assert.assertNull(result.getRenderedOutput());
	}
}