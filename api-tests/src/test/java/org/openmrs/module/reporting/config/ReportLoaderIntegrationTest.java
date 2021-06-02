package org.openmrs.module.reporting.config;

import org.hibernate.cfg.Environment;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.library.BuiltInCohortDefinitionLibrary;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.reporting.config.ReportLoader.getReportingDescriptorsConfigurationDir;

public class ReportLoaderIntegrationTest extends BaseModuleContextSensitiveTest {

    public static final String appDataTestDir = "testAppDataDir";
    
    @Autowired @Qualifier("reportingReportDefinitionService")
    ReportDefinitionService reportDefinitionService;

    @Autowired @Qualifier("reportingEvaluationService")
    EvaluationService evaluationService;

    @Autowired
    BuiltInCohortDefinitionLibrary cohorts;

    @Override
    public Properties getRuntimeProperties() {
        Properties p = super.getRuntimeProperties();
        String path = getClass().getClassLoader().getResource(appDataTestDir).getPath() + File.separator;
        p.put("connection.url", p.getProperty(Environment.URL));
        p.put(Environment.URL, p.getProperty(Environment.URL) + ";MVCC=TRUE");
        p.put("connection.driver_class", p.getProperty(Environment.DRIVER));
        p.setProperty(OpenmrsConstants.APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY, path);
        System.setProperty("OPENMRS_APPLICATION_DATA_DIRECTORY", path);
        return p;
    }

    @Test
    public void shouldLoadAllReportDescriptorsInReportDescriptorsDirectory() {
        List<ReportDescriptor> reportDescriptors = ReportLoader.loadReportDescriptors();
        assertThat(reportDescriptors.size(), is(4));

        List<String> names = new ArrayList<String>();
        for (ReportDescriptor reportDescriptor : reportDescriptors) {
            names.add(reportDescriptor.getName());
        }

        assertThat(names, hasItems("sample.export.encounters.name","sample.export.orders.name", "sample.export.nested.name"));
    }

    @Test
    public void shouldLoadReportsFromConfigAndSave() {
        ReportLoader.loadReportsFromConfig();

        ReportDefinition ordersReportDefinition = reportDefinitionService.getDefinitionByUuid("9e7dc296-2aad-11e3-a840-5b9e0b589afb");
        ReportDefinition encountersReportDefinition = reportDefinitionService.getDefinitionByUuid("752e386d-da67-4e3d-bddc-95157c58c54c");
        ReportDefinition nestedReportDefinition = reportDefinitionService.getDefinitionByUuid("c2fb2082-9b36-4398-96af-d20570bacd07");

        assertThat(ordersReportDefinition, notNullValue());
        assertThat(encountersReportDefinition, notNullValue());
        assertThat(nestedReportDefinition, notNullValue());

        assertThat(ordersReportDefinition.getName(), is("sample.export.orders.name"));
        assertThat(encountersReportDefinition.getName(), is("sample.export.encounters.name"));
        assertThat(nestedReportDefinition.getName(), is("sample.export.nested.name"));

        List<ReportDesign> existingOrderReportDesigns = Context.getService(ReportService.class).getReportDesigns(ordersReportDefinition, null, true);
        assertThat(existingOrderReportDesigns.size(), is(2));
    }

    @Test
    public void shouldSupportFixedParametersInDataSetDefinitions() throws Exception {
        ReportLoader.loadReportsFromConfig();
        ReportDefinition rd = reportDefinitionService.getDefinitionByUuid("0c32f660-c2de-11eb-b5a4-0242ac110002");
        assertThat(rd.getParameters().size(), is(2));
        Mapped<? extends DataSetDefinition> maleMapped = rd.getDataSetDefinitions().get("males");
        Mapped<? extends DataSetDefinition> femaleMapped = rd.getDataSetDefinitions().get("females");
        assertThat(maleMapped.getParameterizable().getParameters().size(), is(3));
        assertThat(femaleMapped.getParameterizable().getParameters().size(), is(3));
        assertThat(maleMapped.getParameterMappings().get("gender").toString(), is("M"));
        assertThat(femaleMapped.getParameterMappings().get("gender").toString(), is("F"));
        ReportData data = reportDefinitionService.evaluate(rd, new EvaluationContext());
        List<Integer> rptMales = new ArrayList<Integer>();
        List<Integer> rptFemales = new ArrayList<Integer>();
        for (DataSetRow row : data.getDataSets().get("males")) {
            rptMales.add((Integer)row.getColumnValue("person_id"));
        }
        for (DataSetRow row : data.getDataSets().get("females")) {
            rptFemales.add((Integer)row.getColumnValue("person_id"));
        }

        SqlQueryBuilder maleQuery = new SqlQueryBuilder("select person_id from person where gender = 'M'");
        List<Integer> males = evaluationService.evaluateToList(maleQuery, Integer.class, new EvaluationContext());

        SqlQueryBuilder femaleQuery = new SqlQueryBuilder("select person_id from person where gender = 'F'");
        List<Integer> females = evaluationService.evaluateToList(femaleQuery, Integer.class, new EvaluationContext());

        assertThat(males.size(), is(rptMales.size()));
        assertThat(females.size(), is(rptFemales.size()));
        assertTrue(males.containsAll(rptMales));
        assertTrue(females.containsAll(rptFemales));
    }

    @Test
    public void shouldConstructExcelReportDesign() {
        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.setName("My Test Report");

        DesignDescriptor designDescriptor = new DesignDescriptor();
        designDescriptor.setType("excel");
        designDescriptor.setTemplate("templates/SampleReportTemplate.xls");

        ReportDescriptor reportDescriptor = new ReportDescriptor();
        reportDescriptor.setPath(new File(getReportingDescriptorsConfigurationDir()));
        reportDescriptor.setDesigns(new ArrayList<DesignDescriptor>());
        reportDescriptor.getDesigns().add(designDescriptor);

        List<ReportDesign> reportDesigns = ReportLoader.constructReportDesigns(reportDefinition, reportDescriptor);
        assertThat(reportDesigns.size(), is(1));
        assertThat(reportDesigns.get(0).getName(), is("reporting.excel"));
        assertThat(reportDesigns.get(0).getRendererType().getName(), endsWith("XlsReportRenderer"));
        assertThat(reportDesigns.get(0).getReportDefinition(), is(reportDefinition));

        assertThat(reportDesigns.get(0).getResources().size(), is(1));
        ReportDesignResource reportDesignResource = reportDesigns.get(0).getResources().iterator().next();
        assertThat(reportDesignResource.getName(), is("template"));
        assertThat(reportDesignResource.getExtension(), is("xls"));
        assertThat(reportDesignResource.getContentType(), is("application/vnd.ms-excel"));
        assertThat(reportDesignResource.getContents(), is(notNullValue()));
    }
}
