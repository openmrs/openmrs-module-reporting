package org.openmrs.module.reporting.config;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.SqlFileDataSetDefinition;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openmrs.module.reporting.config.ReportLoader.getReportingDescriptorsConfigurationDir;

public class ReportLoaderIntegrationTest extends BaseModuleContextSensitiveTest {

    public static final String appDataTestDir = "testAppDataDir";

    private String path;

    @Before
    public void setup() {
        // configure app data dir path
        path = getClass().getClassLoader().getResource(appDataTestDir).getPath() + File.separator;
        System.setProperty("OPENMRS_APPLICATION_DATA_DIRECTORY", path);
        Properties prop = new Properties();
        prop.setProperty(OpenmrsConstants.APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY, path);
        Context.setRuntimeProperties(prop);
    }

    @Test
    public void shouldLoadAllReportDescriptorsInReportDescriptorsDirectory() {
        List<ReportDescriptor> reportDescriptors = ReportLoader.loadReportDescriptors();
        assertThat(reportDescriptors.size(), is(3));

        List<String> names = new ArrayList<String>();
        for (ReportDescriptor reportDescriptor : reportDescriptors) {
            names.add(reportDescriptor.getName());
        }

        assertThat(names, hasItems("sample.export.encounters.name","sample.export.orders.name", "sample.export.nested.name"));
    }

    @Test
    public void shouldConstructSqlFileDataSetDefinition() {
        DataSetDescriptor sqlDataSetDescriptor = new DataSetDescriptor();

        sqlDataSetDescriptor.setKey("encounters");
        sqlDataSetDescriptor.setType("sql");
        sqlDataSetDescriptor.setConfig("sql/encounters.sql");

        SqlFileDataSetDefinition dsd =  (SqlFileDataSetDefinition) ReportLoader.constructDataSetDefinition(sqlDataSetDescriptor, new File(getReportingDescriptorsConfigurationDir()),null);

        assertThat(dsd.getSqlFile(), containsString("encounters.sql"));
    }

    @Test
    public void shouldLoadReportsFromConfigAndSave() {
        ReportLoader.loadReportsFromConfig();

        ReportDefinition ordersReportDefinition = Context.getService(ReportDefinitionService.class).getDefinitionByUuid("9e7dc296-2aad-11e3-a840-5b9e0b589afb");
        ReportDefinition encountersReportDefinition = Context.getService(ReportDefinitionService.class).getDefinitionByUuid("752e386d-da67-4e3d-bddc-95157c58c54c");
        ReportDefinition nestedReportDefinition = Context.getService(ReportDefinitionService.class).getDefinitionByUuid("c2fb2082-9b36-4398-96af-d20570bacd07");

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
