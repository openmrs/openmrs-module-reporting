package org.openmrs.module.reporting.config;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.definition.SqlFileDataSetDefinition;
import org.openmrs.module.reporting.report.ReportDesign;
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
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

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

        SqlFileDataSetDefinition dsd =  (SqlFileDataSetDefinition) ReportLoader.constructDataSetDefinition(sqlDataSetDescriptor, null);

        assertThat(dsd.getSqlFile(), containsString("sql/encounters.sql"));
    }

    @Test
    public void shouldLoadReportsFromConfigAndSave() {
        ReportLoader.loadReportsFromConfig();

        ReportDefinition ordersReportDefinition = Context.getService(ReportDefinitionService.class).getDefinitionByUuid("9e7dc296-2aad-11e3-a840-5b9e0b589afb");
        ReportDefinition encountersReportDefinition = Context.getService(ReportDefinitionService.class).getDefinitionByUuid("752e386d-da67-4e3d-bddc-95157c58c54c");

        assertThat(ordersReportDefinition, notNullValue());
        assertThat(encountersReportDefinition, notNullValue());

        assertThat(ordersReportDefinition.getName(), is("sample.export.orders.name"));
        assertThat(encountersReportDefinition.getName(), is("sample.export.encounters.name"));

        List<ReportDesign> existingOrderReportDesigns = Context.getService(ReportService.class).getReportDesigns(ordersReportDefinition, null, true);
        assertThat(existingOrderReportDesigns.size(), is(1));

    }
}
