package org.openmrs.module.reporting.config;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
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


}
