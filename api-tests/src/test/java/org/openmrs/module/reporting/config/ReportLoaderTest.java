package org.openmrs.module.reporting.config;

import org.junit.Test;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.ReportDesignRenderer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
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
        assertThat(descriptor.getDesigns().get(0).getType(), is("csv"));
        assertThat(descriptor.getDesigns().get(0).getProperties().get("characterEncoding"), is("ISO-8859-1"));
        assertThat(descriptor.getDesigns().get(0).getProperties().get("blacklistRegex"), is("[^\\p{InBasicLatin}\\p{L}]"));
        assertThat(descriptor.getDesigns().get(1).getType(), is("excel"));
        assertThat(descriptor.getDesigns().get(1).getTemplate(), is("ExcelTemplate.xls"));

        assertThat(((List<String>)descriptor.getConfig().get("categories")).size(), is(2));
        assertThat(((List<String>)descriptor.getConfig().get("categories")).get(0), is("DATA_EXPORT"));
        assertThat(((List<String>)descriptor.getConfig().get("categories")).get(1), is("DAILY"));

        assertThat(((List<String>)descriptor.getConfig().get("components")).size(), is(1));
        assertThat(((List<String>)descriptor.getConfig().get("components")).get(0), is("encounters"));

    }

    @Test
    public void shouldConstructParameters() {

        List<ParameterDescriptor> parameterDescriptors = new ArrayList<ParameterDescriptor>();

        ParameterDescriptor startDate = new ParameterDescriptor();
        startDate.setKey("startDate");
        startDate.setLabel("startDate.label");
        startDate.setType("date");

        ParameterDescriptor endDate = new ParameterDescriptor();
        endDate.setKey("endDate");
        endDate.setLabel("endDate.label");
        endDate.setType("java.util.Date");

        ParameterDescriptor location = new ParameterDescriptor();
        location.setKey("location");
        location.setLabel("location.label");
        location.setType("Location");

        parameterDescriptors.add(startDate);
        parameterDescriptors.add(endDate);
        parameterDescriptors.add(location);

        List<Parameter> parameters = ReportLoader.constructParameters(parameterDescriptors);

        assertThat(parameters.size(), is(3));
        assertThat(parameters.get(0).getLabel(), is("startDate.label"));
        assertThat(parameters.get(0).getName(), is("startDate"));
        assertThat(parameters.get(0).getType().getName(), is("java.util.Date"));

        assertThat(parameters.get(1).getLabel(), is("endDate.label"));
        assertThat(parameters.get(1).getName(), is("endDate"));
        assertThat(parameters.get(1).getType().getName(), is("java.util.Date"));

        assertThat(parameters.get(2).getLabel(), is("location.label"));
        assertThat(parameters.get(2).getName(), is("location"));
        assertThat(parameters.get(2).getType().getName(), is("org.openmrs.Location"));

    }

    @Test
    public void shouldConstructMappings() {
        List<ParameterDescriptor> parameterDescriptors = new ArrayList<ParameterDescriptor>();

        ParameterDescriptor date = new ParameterDescriptor();
        date.setKey("startDate");
        date.setLabel("startDate.label");
        date.setType("java.util.Date");

        ParameterDescriptor location = new ParameterDescriptor();
        location.setKey("location");
        location.setLabel("location.label");
        location.setType("org.openmrs.Location");

        parameterDescriptors.add(date);
        parameterDescriptors.add(location);

        List<Parameter> parameters = ReportLoader.constructParameters(parameterDescriptors);

        Map<String, Object> mappings = ReportLoader.constructMappings(parameters);
        assertThat((String) mappings.get("startDate"), is("${startDate}"));
        assertThat((String) mappings.get("location"), is("${location}"));
    }

    @Test
    public void shouldConstructCSVReportDesign() {
        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.setName("My Test Report");

        DesignDescriptor designDescriptor = new DesignDescriptor();
        designDescriptor.setType("csv");
        designDescriptor.setProperties(new HashMap<String, String>());
        designDescriptor.getProperties().put("characterEncoding", "ISO-8859-1");
        designDescriptor.getProperties().put("blacklistRegex", "[^\\p{InBasicLatin}\\p{L}]");

        ReportDescriptor reportDescriptor = new ReportDescriptor();
        reportDescriptor.setDesigns(new ArrayList<DesignDescriptor>());
        reportDescriptor.getDesigns().add(designDescriptor);

        List<ReportDesign> reportDesigns = ReportLoader.constructReportDesigns(reportDefinition, reportDescriptor);
        assertThat(reportDesigns.size(), is(1));
        assertThat(reportDesigns.get(0).getName(), is("reporting.csv"));
        assertThat(reportDesigns.get(0).getReportDefinition(), is(reportDefinition));
        assertThat(reportDesigns.get(0).getPropertyValue(ReportDesignRenderer.FILENAME_BASE_PROPERTY, null), startsWith("my.test.report."));
        assertThat(reportDesigns.get(0).getPropertyValue("characterEncoding", null), is("ISO-8859-1"));
        assertThat(reportDesigns.get(0).getPropertyValue("blacklistRegex", null), is("[^\\p{InBasicLatin}\\p{L}]"));

    }

    @Test
    public void shouldConstructCSVReportDesignIfNoDesignSpecified() {
        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.setName("My Test Report");

        ReportDescriptor reportDescriptor = new ReportDescriptor();

        List<ReportDesign> reportDesigns = ReportLoader.constructReportDesigns(reportDefinition, reportDescriptor);
        assertThat(reportDesigns.size(), is(1));
        assertThat(reportDesigns.get(0).getName(), is("reporting.csv"));
        assertThat(reportDesigns.get(0).getRendererType().getName(), endsWith("CsvReportRenderer"));
        assertThat(reportDesigns.get(0).getReportDefinition(), is(reportDefinition));
        assertThat(reportDesigns.get(0).getPropertyValue(ReportDesignRenderer.FILENAME_BASE_PROPERTY, null), startsWith("my.test.report."));
    }

}
