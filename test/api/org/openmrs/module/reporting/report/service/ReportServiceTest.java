package org.openmrs.module.reporting.report.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.ReportProcessorConfiguration;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Priority;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.processor.LoggingReportProcessor;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.renderer.TsvReportRenderer;
import org.openmrs.module.reporting.web.renderers.IndicatorReportWebRenderer;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class ReportServiceTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
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
		ReportRequest request = new ReportRequest(new Mapped<ReportDefinition>(def, null), null, null, Priority.NORMAL, null);
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
		ReportRequest request = new ReportRequest(new Mapped<ReportDefinition>(def, null), null, new RenderingMode(renderer, "TSV", null, 100), Priority.NORMAL, null);
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
		ReportRequest request = new ReportRequest(new Mapped<ReportDefinition>(def, null), null, new RenderingMode(renderer, "Web", null, 100), Priority.NORMAL, null);
		Report result = Context.getService(ReportService.class).runReport(request);
		Assert.assertNotNull(result.getReportData());
		Assert.assertNull(result.getRenderedOutput());
	}
	

	/**
	 * @see ReportService#saveReportProcessorConfiguration(ReportProcessorConfiguration)
	 * @verifies save a report processor configuration
	 */
	@Test
	public void saveReportProcessorConfiguration_shouldSaveAReportProcessorConfiguration() throws Exception {
		ReportService rs = Context.getService(ReportService.class);
		ReportProcessorConfiguration c = new ReportProcessorConfiguration();
		c.setName("New Processor");
		c.setProcessorType(LoggingReportProcessor.class);
		c = rs.saveReportProcessorConfiguration(c);
		Assert.assertNotNull(c.getId());
		Assert.assertNotNull(c.getUuid());
		Assert.assertEquals(3, rs.getAllReportProcessorConfigurations(true).size());
	}

	/**
	 * @see ReportService#getAllReportProcessorConfigurations(boolean)
	 * @verifies retrieve all saved report processor configurations including retired if specified
	 */
	@Test
	public void getAllReportProcessorConfigurations_shouldRetrieveAllSavedReportProcessorConfigurationsIncludingRetiredIfSpecified() throws Exception {
		ReportService rs = Context.getService(ReportService.class);
		Assert.assertEquals(2, rs.getAllReportProcessorConfigurations(true).size());
		Assert.assertEquals(1, rs.getAllReportProcessorConfigurations(false).size());
	}

	/**
	 * @see ReportService#getReportProcessorConfiguration(Integer)
	 * @verifies retrieve a saved report processor configuration by id
	 */
	@Test
	public void getReportProcessorConfiguration_shouldRetrieveASavedReportProcessorConfigurationById() throws Exception {
		ReportService rs = Context.getService(ReportService.class);
		ReportProcessorConfiguration c = rs.getReportProcessorConfiguration(2);
		Assert.assertEquals("Logging processor", c.getName());
	}

	/**
	 * @see ReportService#getReportProcessorConfigurationByUuid(String)
	 * @verifies retrieve a saved report processor configuration by uuid
	 */
	@Test
	public void getReportProcessorConfigurationByUuid_shouldRetrieveASavedReportProcessorConfigurationByUuid() throws Exception {
		ReportService rs = Context.getService(ReportService.class);
		ReportProcessorConfiguration c = rs.getReportProcessorConfigurationByUuid("c11117dd-4478-4a0e-84fe-ee62c5f0676a");
		Assert.assertEquals("Logging processor", c.getName());
	}

	/**
	 * @see ReportService#getReportProcessorConfigurations(Class)
	 * @verifies retrieve all non-retired report processor configurations that are assignable to the passed type
	 */
	@Test
	public void getReportProcessorConfigurations_shouldRetrieveAllNonretiredReportProcessorConfigurationsThatAreAssignableToThePassedType() throws Exception {
		ReportService rs = Context.getService(ReportService.class);
		Assert.assertEquals(1, rs.getReportProcessorConfigurations(LoggingReportProcessor.class).size());
	}

	/**
	 * @see ReportService#purgeReportProcessorConfiguration(ReportProcessorConfiguration)
	 * @verifies delete a saved report processor configuration
	 */
	@Test
	public void purgeReportProcessorConfiguration_shouldDeleteASavedReportProcessorConfiguration() throws Exception {
		ReportService rs = Context.getService(ReportService.class);
		ReportProcessorConfiguration c = rs.getReportProcessorConfiguration(1);
		rs.purgeReportProcessorConfiguration(c);
		Assert.assertEquals(1, rs.getAllReportProcessorConfigurations(true).size());
	}

	/**
	 * @see ReportService#runReport(ReportRequest)
	 * @verifies execute any configured report processors
	 */
	@Test
	public void runReport_shouldExecuteAnyConfiguredReportProcessors() throws Exception {
		
		ReportDefinition def = new ReportDefinition();
		def.setName("My report");
		SqlDataSetDefinition dsd = new SqlDataSetDefinition();
		dsd.setSqlQuery("select count(*) from patient");
		def.addDataSetDefinition("patients", dsd, null);
		Context.getService(ReportDefinitionService.class).saveDefinition(def);
		
		RenderingMode rm = new RenderingMode(new TsvReportRenderer(), "TSV", null, 100);

		ReportRequest request = new ReportRequest(new Mapped<ReportDefinition>(def, null), null, rm, Priority.NORMAL, null);
		request.addReportProcessor(new ReportProcessorConfiguration("Logging Processor", LoggingReportProcessor.class, null, true, true));
		
		/*
		Properties emailProperties = new Properties();
		emailProperties.put("from", "mseaton@pih.org");
		emailProperties.put("to", "mseaton@pih.org");
		emailProperties.put("subject", "Mail with content as attachment");
		emailProperties.put("content", "This is the content of the email");
		emailProperties.put("addOutputAsAttachment", "true");
		emailProperties.put("attachmentName", "ReportTemplate");
		request.addReportProcessor(new ReportProcessorConfiguration("Email Processor", EmailReportProcessor.class, emailProperties, true, true));
		
		Properties emailProperties2 = new Properties();
		emailProperties2.put("from", "mseaton@pih.org");
		emailProperties2.put("to", "mseaton@pih.org");
		emailProperties2.put("subject", "Mail with content as body");
		emailProperties2.put("content", "This is the content of the email:<br/><br/>");
		emailProperties2.put("addOutputToContent", "true");
		request.addReportProcessor(new ReportProcessorConfiguration("Email Processor 2", EmailReportProcessor.class, emailProperties2, true, true));
		*/
		Context.getService(ReportService.class).runReport(request);
	}

}