package org.openmrs.module.reporting.report.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportProcessorConfiguration;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Priority;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.processor.LoggingReportProcessor;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.renderer.TsvReportRenderer;
import org.openmrs.module.reporting.web.renderers.DefaultWebRenderer;
import org.openmrs.module.reporting.web.renderers.WebReportRenderer;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class ReportServiceTest extends BaseModuleContextSensitiveTest {

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

	@Test
	public void shouldSaveReportDefinition() throws Exception {
		ReportDefinitionService service = Context.getService(ReportDefinitionService.class);
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("Testing");
		ReportDefinition savedReportDefinition = service.saveDefinition(reportDefinition);
		Assert.assertTrue(savedReportDefinition.getId() != null);
	}

	/**
	 * @see {@link ReportService#runReport(ReportRequest)}
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
		Assert.assertNotNull(result.getRenderedOutput());
	}

	/**
	 * @see {@link ReportService#runReport(ReportRequest)}
	 */
	@Test
	@Verifies(value = "should not render the report if a web renderer is specified", method = "runReport(ReportRequest)")
	public void runReport_shouldNotRenderTheReportIfAWebRendererIsSpecified() throws Exception {
		ReportDefinition def = new ReportDefinition();
		WebReportRenderer renderer = new DefaultWebRenderer();
		ReportRequest request = new ReportRequest(new Mapped<ReportDefinition>(def, null), null, new RenderingMode(renderer, "Web", null, 100), Priority.NORMAL, null);
		Report result = Context.getService(ReportService.class).runReport(request);
		Assert.assertNotNull(result.getReportData());
		Assert.assertNull(result.getRenderedOutput());
	}

	/**
	 * @see {@link ReportService#runReport(ReportRequest)}
	 */
	@Test
	@Verifies(value = "should allow dynamic parameters", method = "runReport(ReportRequest)")
	public void runReport_shouldAllowDynamicParameters() throws Exception {
		ReportDefinition rptDef = new ReportDefinition();
		rptDef.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		SqlDataSetDefinition sqlDef = new SqlDataSetDefinition("test sql dsd", null, "select person_id, birthdate from person where birthdate < :effectiveDate");
		sqlDef.addParameter(new Parameter("effectiveDate", "Effective Date", Date.class));
		rptDef.addDataSetDefinition(sqlDef, ParameterizableUtil.createParameterMappings("effectiveDate=${effectiveDate}"));

		RenderingMode mode = new RenderingMode(new CsvReportRenderer(), "CSV", null, 100);

		Mapped<ReportDefinition> mappedReport = new Mapped<ReportDefinition>();
		mappedReport.setParameterizable(rptDef);
		mappedReport.addParameterMapping("effectiveDate", "${now-50y}");
		ReportRequest request = new ReportRequest(mappedReport, null, mode, Priority.HIGHEST, null);
		Report report = Context.getService(ReportService.class).runReport(request);
		String s = new String(report.getRenderedOutput());
	}


	/**
	 * @verifies save a report processor configuration
	 * @see ReportService#saveReportProcessorConfiguration(ReportProcessorConfiguration)
	 */
	@Test
	public void saveReportProcessorConfiguration_shouldSaveAReportProcessorConfiguration() throws Exception {
		ReportService rs = Context.getService(ReportService.class);
		ReportProcessorConfiguration c = new ReportProcessorConfiguration();
		c.setName("New Processor");
		c.setProcessorType(LoggingReportProcessor.class.getName());
		c = rs.saveReportProcessorConfiguration(c);
		Assert.assertNotNull(c.getId());
		Assert.assertNotNull(c.getUuid());
		Assert.assertEquals(3, rs.getAllReportProcessorConfigurations(true).size());
	}

	/**
	 * @verifies retrieve all saved report processor configurations including retired if specified
	 * @see ReportService#getAllReportProcessorConfigurations(boolean)
	 */
	@Test
	public void getAllReportProcessorConfigurations_shouldRetrieveAllSavedReportProcessorConfigurationsIncludingRetiredIfSpecified() throws Exception {
		ReportService rs = Context.getService(ReportService.class);
		Assert.assertEquals(2, rs.getAllReportProcessorConfigurations(true).size());
		Assert.assertEquals(1, rs.getAllReportProcessorConfigurations(false).size());
	}

	/**
	 * @verifies retrieve a saved report processor configuration by id
	 * @see ReportService#getReportProcessorConfiguration(Integer)
	 */
	@Test
	public void getReportProcessorConfiguration_shouldRetrieveASavedReportProcessorConfigurationById() throws Exception {
		ReportService rs = Context.getService(ReportService.class);
		ReportProcessorConfiguration c = rs.getReportProcessorConfiguration(2);
		Assert.assertEquals("Logging processor", c.getName());
	}

	/**
	 * @verifies retrieve a saved report processor configuration by uuid
	 * @see ReportService#getReportProcessorConfigurationByUuid(String)
	 */
	@Test
	public void getReportProcessorConfigurationByUuid_shouldRetrieveASavedReportProcessorConfigurationByUuid() throws Exception {
		ReportService rs = Context.getService(ReportService.class);
		ReportProcessorConfiguration c = rs.getReportProcessorConfigurationByUuid("c11117dd-4478-4a0e-84fe-ee62c5f0676a");
		Assert.assertEquals("Logging processor", c.getName());
	}

	/**
	 * @verifies retrieve all non-retired report processor configurations that are assignable to the passed type
	 * @see ReportService#getReportProcessorConfigurations(Class)
	 */
	@Test
	public void getReportProcessorConfigurations_shouldRetrieveAllNonretiredReportProcessorConfigurationsThatAreAssignableToThePassedType() throws Exception {
		ReportService rs = Context.getService(ReportService.class);
		Assert.assertEquals(1, rs.getReportProcessorConfigurations(LoggingReportProcessor.class).size());
	}

	/**
	 * @verifies delete a saved report processor configuration
	 * @see ReportService#purgeReportProcessorConfiguration(ReportProcessorConfiguration)
	 */
	@Test
	public void purgeReportProcessorConfiguration_shouldDeleteASavedReportProcessorConfiguration() throws Exception {
		ReportService rs = Context.getService(ReportService.class);
		ReportProcessorConfiguration c = rs.getReportProcessorConfiguration(1);
		rs.purgeReportProcessorConfiguration(c);
		Assert.assertEquals(1, rs.getAllReportProcessorConfigurations(true).size());
	}

	@Test
	@Verifies(value = "should retrieve all global processors after creating a non-global processor", method = "getGlobalReportProcessor")
	public void shouldRetrieveAllGlobalProcessors() throws Exception {


		//now we should have three total ReportProcessorConfigs in the db, 2 of which don't have reportDesign set (the two in the dbunit file), meaning that they're global.
		// but 1 is retired, so there should only be 1
		List<ReportProcessorConfiguration> ret = Context.getService(ReportService.class).getGlobalReportProcessorConfigurations();
		Assert.assertTrue(ret.size() == 1);
	}

	@Test
	@Verifies(value = "should retrieve all global processors after creating a non-global processor", method = "getGlobalReportProcessor")
	public void shouldRetrieveAllGlobalProcessorsAfterAddingGlobalProcessor() throws Exception {

		//create a report processor config
		Properties props = new Properties();
		ReportProcessorConfiguration procConfig = new ReportProcessorConfiguration("Test Processor", LoggingReportProcessor.class, props, true, true);
		String procUuid = UUID.randomUUID().toString();
		procConfig.setUuid(procUuid);
		procConfig.setProcessorMode(ReportProcessorConfiguration.ProcessorMode.ON_DEMAND_AND_AUTOMATIC);
		Context.getService(ReportService.class).saveReportProcessorConfiguration(procConfig);

		//there was 1 to start with, now there should be 2
		List<ReportProcessorConfiguration> ret = Context.getService(ReportService.class).getGlobalReportProcessorConfigurations();
		Assert.assertTrue(ret.size() == 2);
	}

	/**
	 * @verifies execute any configured report processors
	 * @see ReportService#runReport(ReportRequest)
	 */
	@Test
	public void runReport_shouldExecuteTestReportProcessor() throws Exception {

		ReportDefinition def = new ReportDefinition();
		def.setName("My report");
		SqlDataSetDefinition dsd = new SqlDataSetDefinition();
		dsd.setSqlQuery("select count(*) from patient");
		def.addDataSetDefinition("patients", dsd, null);
		Context.getService(ReportDefinitionService.class).saveDefinition(def);

		RenderingMode rm = new RenderingMode(new TsvReportRenderer(), "TSV", null, 100);
		ReportRequest request = new ReportRequest(new Mapped<ReportDefinition>(def, null), null, rm, Priority.NORMAL, null);
		request.setProcessAutomatically(true);

		//build a processor
		Properties props = new Properties();
		ReportProcessorConfiguration procConfig = new ReportProcessorConfiguration("LoggingProcessorTest", TestReportProcessor.class, props, true, true);
		String procUuid = UUID.randomUUID().toString();
		procConfig.setUuid(procUuid);
		procConfig.setProcessorMode(ReportProcessorConfiguration.ProcessorMode.AUTOMATIC); //test processor can run because processing mode is automatic

		//create and save a report  design, containing the processor
		ReportDesign rd = new ReportDesign();
		rd.setName("myReportDesign");
		rd.addReportProcessor(procConfig);
		rd.setReportDefinition(def);
		rd.setRendererType(TsvReportRenderer.class);
		String uuid = UUID.randomUUID().toString();
		rd.setUuid(uuid);
		Context.getService(ReportService.class).saveReportDesign(rd);

		//run the report
		Report report = Context.getService(ReportService.class).runReport(request);
		//TestReportProcessor is a simple processor that set a report error message -- just a simple way to ensure the processor was run...
		Assert.assertTrue(report.getErrorMessage().equals("TestReportProcessor.process was called corretly."));

		//sanity check on global processors -- the one we create here isn't global, so there should only be 1
		List<ReportProcessorConfiguration> ret = Context.getService(ReportService.class).getGlobalReportProcessorConfigurations();
		Assert.assertTrue(ret.size() == 1);
	}

	/**
	 * @verifies execute any configured report processors
	 * @see ReportService#runReport(ReportRequest)
	 */
	@Test
	public void runReport_shouldNotExecuteTestReportProcessorDifferentRenderers() throws Exception {

		ReportDefinition def = new ReportDefinition();
		def.setName("My report");
		SqlDataSetDefinition dsd = new SqlDataSetDefinition();
		dsd.setSqlQuery("select count(*) from patient");
		def.addDataSetDefinition("patients", dsd, null);
		Context.getService(ReportDefinitionService.class).saveDefinition(def);

		RenderingMode rm = new RenderingMode(new TsvReportRenderer(), "TSV", null, 100);
		ReportRequest request = new ReportRequest(new Mapped<ReportDefinition>(def, null), null, rm, Priority.NORMAL, null);

		//build a processor
		Properties props = new Properties();
		ReportProcessorConfiguration procConfig = new ReportProcessorConfiguration("LoggingProcessorTest", TestReportProcessor.class, props, true, true);
		String procUuid = UUID.randomUUID().toString();
		procConfig.setUuid(procUuid);
		procConfig.setProcessorMode(ReportProcessorConfiguration.ProcessorMode.AUTOMATIC); //test processor can run because processing mode is automatic

		//create and save a report  design, containing the processor
		ReportDesign rd = new ReportDesign();
		rd.setName("myReportDesign");
		rd.addReportProcessor(procConfig);
		rd.setReportDefinition(def);
		rd.setRendererType(CsvReportRenderer.class); // test processor won't run because report request is Tsv, reportDefinition is Csv
		String uuid = UUID.randomUUID().toString();
		rd.setUuid(uuid);
		Context.getService(ReportService.class).saveReportDesign(rd);

		//run the report
		Report report = Context.getService(ReportService.class).runReport(request);
		//TestReportProcessor is a simple processor that set a report error message -- just a simple way to ensure the processor was run...
		Assert.assertTrue(report.getErrorMessage() == null);
	}

	/**
	 * @verifies execute any configured report processors
	 * @see ReportService#runReport(ReportRequest)
	 */
	@Test
	public void runReport_shouldNotExecuteTestReportProcessorNotAutomatic() throws Exception {

		ReportDefinition def = new ReportDefinition();
		def.setName("My report");
		SqlDataSetDefinition dsd = new SqlDataSetDefinition();
		dsd.setSqlQuery("select count(*) from patient");
		def.addDataSetDefinition("patients", dsd, null);
		Context.getService(ReportDefinitionService.class).saveDefinition(def);

		RenderingMode rm = new RenderingMode(new TsvReportRenderer(), "TSV", null, 100);
		ReportRequest request = new ReportRequest(new Mapped<ReportDefinition>(def, null), null, rm, Priority.NORMAL, null);

		//build a processor
		Properties props = new Properties();
		ReportProcessorConfiguration procConfig = new ReportProcessorConfiguration("LoggingProcessorTest", TestReportProcessor.class, props, true, true);
		String procUuid = UUID.randomUUID().toString();
		procConfig.setUuid(procUuid);
		procConfig.setProcessorMode(ReportProcessorConfiguration.ProcessorMode.ON_DEMAND); //test processor won't be run because its not automatic

		//create and save a report  design, containing the processor
		ReportDesign rd = new ReportDesign();
		rd.setName("myReportDesign");
		rd.addReportProcessor(procConfig);
		rd.setReportDefinition(def);
		rd.setRendererType(TsvReportRenderer.class);
		String uuid = UUID.randomUUID().toString();
		rd.setUuid(uuid);
		Context.getService(ReportService.class).saveReportDesign(rd);

		//run the report
		Report report = Context.getService(ReportService.class).runReport(request);
		//TestReportProcessor is a simple processor that set a report error message -- just a simple way to ensure the processor was run...
		Assert.assertTrue(report.getErrorMessage() == null);
	}

	@Test
	@Verifies(value = "should save the ReportProcessor", method = "saveReportDesign(ReportDesign)")
	public void shouldSaveReportDefinitionWithProcessor() throws Exception {

		//save a blank report definition
		ReportDefinitionService service = Context.getService(ReportDefinitionService.class);
		ReportService rs = Context.getService(ReportService.class);
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setName("Testing");
		service.saveDefinition(reportDefinition);

		//create a report processor config
		Properties props = new Properties();
		ReportProcessorConfiguration procConfig = new ReportProcessorConfiguration("LoggingProcessorTest", LoggingReportProcessor.class, props, true, true);
		String procUuid = UUID.randomUUID().toString();
		procConfig.setUuid(procUuid);
		procConfig.setProcessorMode(ReportProcessorConfiguration.ProcessorMode.ON_DEMAND_AND_AUTOMATIC);

		//create and save a report  design, containing the processor
		ReportDesign rd = new ReportDesign();
		rd.setName("myReportDesign");
		rd.addReportProcessor(procConfig);
		rd.setReportDefinition(reportDefinition);
		rd.setRendererType(CsvReportRenderer.class);
		String uuid = UUID.randomUUID().toString();
		rd.setUuid(uuid);
		rs.saveReportDesign(rd);

		//retreive and verify the processor
		ReportProcessorConfiguration rpc = rs.getReportProcessorConfigurationByUuid(procUuid);
		Assert.assertTrue(rpc != null);
		Assert.assertTrue(rpc.getProcessorMode().equals(ReportProcessorConfiguration.ProcessorMode.ON_DEMAND_AND_AUTOMATIC));
		rpc = null;

		//retrieve and verify that the processor is retreived with ReportDesign
		ReportDesign ret = rs.getReportDesignByUuid(uuid);
		Assert.assertTrue(ret != null);
		Assert.assertTrue(ret.getReportProcessors().size() == 1);
		ReportProcessorConfiguration rp = ret.getReportProcessors().iterator().next();
		Assert.assertTrue(rp.getProcessorMode().equals(ReportProcessorConfiguration.ProcessorMode.ON_DEMAND_AND_AUTOMATIC));

	}

	@Test
	@Verifies(value = "readProcessorModeCorrectly", method = "getReportProcessorConfiguration(id)")
	public void shouldReadProcessorModeEnumCorrectly() throws Exception {
		ReportService rs = Context.getService(ReportService.class);
		ReportProcessorConfiguration rpc = rs.getReportProcessorConfiguration(1);
		Assert.assertTrue(rpc.getProcessorMode().equals(ReportProcessorConfiguration.ProcessorMode.DISABLED));

		rpc = rs.getReportProcessorConfiguration(2);
		Assert.assertTrue(rpc.getProcessorMode().equals(ReportProcessorConfiguration.ProcessorMode.AUTOMATIC));
	}

	/**
	 * @verifies set the evaluationDate on the context from the request
	 * @see ReportService#runReport(org.openmrs.module.reporting.report.ReportRequest)
	 */
	@Test
	public void runReport_shouldSetTheEvaluationDateOnTheContextFromTheRequest() throws Exception {
		ReportDefinition def = new ReportDefinition();
		ReportRequest request = new ReportRequest(new Mapped<ReportDefinition>(def, null), null, null, Priority.NORMAL, null);
		Calendar c = Calendar.getInstance();
		c.set(1975, Calendar.OCTOBER, 16);
		request.setEvaluationDate(c.getTime());
		Report actual = Context.getService(ReportService.class).runReport(request);
		Assert.assertEquals(actual.getReportData().getContext().getEvaluationDate(), c.getTime());
	}

	/**
	 * @verifies use current date as evaluationDate if not provided by the request
	 * @see ReportService#runReport(org.openmrs.module.reporting.report.ReportRequest)
	 */
	@Test
	public void runReport_shouldUseCurrentDateAsEvaluationDateIfNotProvidedByTheRequest() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		ReportDefinition def = new ReportDefinition();
		ReportRequest request = new ReportRequest(new Mapped<ReportDefinition>(def, null), null, null, Priority.NORMAL, null);
		Report actual = Context.getService(ReportService.class).runReport(request);
		Assert.assertEquals(sdf.format(actual.getReportData().getContext().getEvaluationDate()), sdf.format(new Date()));
	}

	@Test
	public void saveReport_shouldSaveSuccessfullyIfNotCached() throws Exception {
		ReportDefinition def = new ReportDefinition();
		SqlDataSetDefinition dsd = new SqlDataSetDefinition();
		dsd.setSqlQuery("select count(*) from patient");
		def.addDataSetDefinition("patients", dsd, null);
		ReportRenderer renderer = new TsvReportRenderer();
		ReportRequest request = new ReportRequest(new Mapped<ReportDefinition>(def, null), null, new RenderingMode(renderer, "TSV", null, 100), Priority.NORMAL, null);
		Report result = Context.getService(ReportService.class).runReport(request);
		Context.getService(ReportService.class).saveReport(result, "Test Saving");
	}
}