package org.openmrs.module.reporting.config;

import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReportLoaderTest {

    @Test
    public void load_shouldLoadYAMLFile() {

        ClassLoader classLoader = getClass().getClassLoader();
        File sampleReport = new File(classLoader.getResource("config/sampleReport.yml").getFile());

        ReportDescriptor descriptor = ReportLoader.load(sampleReport);

        assertThat(descriptor.getKey(), is("sampledataexport"));
        assertThat(descriptor.getUuid(), is("9e7dc296-2aad-11e3-a840-5b9e0b589afb"));
        assertThat(descriptor.getName(), is("sample.export.name"));
        assertThat(descriptor.getDescription(), is("sample.export.description"));
        assertThat(descriptor.getParameters().size(), is(2));
        assertThat(descriptor.getParameters().get(0).getKey(), is("startDate"));
        assertThat(descriptor.getParameters().get(0).getType(), is("date"));
        assertThat(descriptor.getParameters().get(0).getLabel(), is("startDate.label"));
        assertThat(descriptor.getDatasets().size(), is(2));
        assertThat(descriptor.getDatasets().get(0).getKey(), is("orders"));
        assertThat(descriptor.getDatasets().get(0).getType(), is("sql"));
        assertThat(descriptor.getDatasets().get(0).getConfig(), is("orders.sql"));

        assertThat(((List<String>)descriptor.getConfig().get("categories")).size(), is(2));
        assertThat(((List<String>)descriptor.getConfig().get("categories")).get(0), is("DATA_EXPORT"));
        assertThat(((List<String>)descriptor.getConfig().get("categories")).get(1), is("DAILY"));

        assertThat(((List<String>)descriptor.getConfig().get("components")).size(), is(1));
        assertThat(((List<String>)descriptor.getConfig().get("components")).get(0), is("encounters"));

    }

}
