package org.openmrs.module.reporting.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.reporting.common.ContentType;
import org.openmrs.module.reporting.config.factory.DataSetFactory;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.ReportProcessorConfiguration;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.processor.DiskReportProcessor;
import org.openmrs.module.reporting.report.processor.EmailReportProcessor;
import org.openmrs.module.reporting.report.processor.LoggingReportProcessor;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.report.renderer.ReportDesignRenderer;
import org.openmrs.module.reporting.report.renderer.XlsReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class ReportLoader {

    private static Log log = LogFactory.getLog(ReportLoader.class);

    public static final String getReportingDescriptorsConfigurationDir() {
        return OpenmrsUtil.getApplicationDataDirectory() +  File.separator + "configuration" +  File.separator + "reports" +  File.separator + "reportdescriptors";
    }

    public static void loadReportsFromConfig() {
        for (ReportDescriptor reportDescriptor : loadReportDescriptors()) {
            loadReportFromDescriptor(reportDescriptor);
        }
    }

    public static void loadReportFromDescriptor(ReportDescriptor reportDescriptor) {
        ReportDefinition reportDefinition = constructReportDefinition(reportDescriptor);
        saveReportDefinition(reportDefinition);
        List<ReportDesign> reportDesigns = constructReportDesigns(reportDefinition, reportDescriptor);
        saveReportDesigns(reportDefinition, reportDesigns);
    }

    public static void saveReportDefinition(ReportDefinition reportDefinition) {

        ReportDefinition existing = Context.getService(ReportDefinitionService.class).getDefinitionByUuid(reportDefinition.getUuid());
        if (existing != null) {
            // we need to overwrite the existing, rather than purge-and-recreate, to avoid deleting old ReportRequests
            reportDefinition.setId(existing.getId());
        }
        else {
            // incompatible class changes for a serialized object could mean that getting the definition return null
            // and some serialization error gets logged. In that case we want to overwrite the invalid serialized definition
            SerializedObject invalidSerializedObject = Context.getRegisteredComponents(SerializedObjectDAO.class).get(0).getSerializedObjectByUuid(reportDefinition.getUuid());
            if (invalidSerializedObject != null) {
                reportDefinition.setId(invalidSerializedObject.getId());
            }
        }

        Context.getService(ReportDefinitionService.class).saveDefinition(reportDefinition);
    }

    public static void saveReportDesigns(ReportDefinition reportDefinition, List<ReportDesign> reportDesigns) {
        // purging a ReportDesign doesn't trigger any extra logic, so we can just purge-and-recreate here
        List<ReportDesign> existingDesigns = Context.getService(ReportService.class).getReportDesigns(reportDefinition, null, true);
        if (!existingDesigns.isEmpty()) {
            log.debug("Deleting " + existingDesigns.size() + " old designs for " + reportDefinition.getName());
            for (ReportDesign design : existingDesigns) {
                Context.getService(ReportService.class).purgeReportDesign(design);
            }
        }

        for (ReportDesign reportDesign : reportDesigns) {
            Context.getService(ReportService.class).saveReportDesign(reportDesign);
        }
    }

    public static  ReportDefinition constructReportDefinition(ReportDescriptor reportDescriptor) {
        ReportDefinition rd = new ReportDefinition();
        rd.setName(reportDescriptor.getName());
        rd.setDescription(reportDescriptor.getDescription());
        rd.setUuid(reportDescriptor.getUuid());
        rd.setParameters(constructParameters(reportDescriptor.getParameters()));

        if (reportDescriptor.getDatasets() != null) {
            for (DataSetDescriptor dataSetDescriptor : reportDescriptor.getDatasets()) {
                addDataSetDefinition(rd, dataSetDescriptor, reportDescriptor.getPath());
            }
        }
        return rd;
    }

    public static List<Parameter> constructParameters(List<ParameterDescriptor> parameterDescriptors) {
        List<Parameter> parameters = new ArrayList<Parameter>();
        if (parameterDescriptors != null) {
            for (ParameterDescriptor parameterDescriptor : parameterDescriptors) {
                try {
                    Class parameterType = getParameterClass(parameterDescriptor.getType());
                    Parameter p = new Parameter();
                    p.setName(parameterDescriptor.getKey());
                    p.setLabel(parameterDescriptor.getLabel());
                    p.setType(parameterType);
                    p.setDefaultValue(getParameterValue(parameterType, parameterDescriptor.getValue()));
                    p.setRequired(BooleanUtils.isNotFalse(parameterDescriptor.getRequired()));
                    p.setWidgetConfiguration(parameterDescriptor.getWidgetConfiguration());
                    parameters.add(p);
                } catch (Exception e) {
                    throw new RuntimeException("Unable to configure parameter " + parameterDescriptor.getKey(), e);
                }
            }
        }
        return parameters;
    }

    public static Class<?> getParameterClass(String clazz) throws ClassNotFoundException {
        if (clazz == null) {
            return String.class;
        }
        if (clazz.equalsIgnoreCase("location")) {
            return Location.class;
        }
        else if (clazz.equalsIgnoreCase("date")) {
            return Date.class;
        }
        else if (clazz.equalsIgnoreCase("text") || clazz.equalsIgnoreCase("string")) {
            return String.class;
        }
        else if (clazz.equalsIgnoreCase("locale")) {
            return Locale.class;
        }
        else {
            return Context.loadClass(clazz);
        }
    }

    public static Object getParameterValue(Class parameterType, String stringVal) {
        Object ret = null;
        try {
            if (stringVal != null) {
                if (parameterType == String.class) {
                    return stringVal;
                }
                else if (parameterType == Date.class) {
                    return new SimpleDateFormat("yyyy-MM-dd").parse(stringVal);
                }
                else if (parameterType == Locale.class) {
                    return new Locale(stringVal);
                }
                else {
                    throw new IllegalStateException("Unable to parse parameter values of type " + parameterType);
                }
            }
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to parse parameter value " + stringVal + " to a " + parameterType);
        }
        return ret;
    }

    public static void addDataSetDefinition(ReportDefinition rd, DataSetDescriptor dataSetDescriptor, File path) {

        Mapped<DataSetDefinition> mappedDsd = new Mapped<DataSetDefinition>();

        String factoryBeanName = dataSetDescriptor.getType();
        if ("sql".equalsIgnoreCase(factoryBeanName)) {
            factoryBeanName = "sqlDataSetFactory";
        }

        DataSetFactory factory = Context.getRegisteredComponent(factoryBeanName, DataSetFactory.class);
        if (factory == null) {
            throw new RuntimeException("Unsupported data set descriptor type: " + dataSetDescriptor.getType());
        }
        DataSetDefinition dsd = factory.constructDataSetDefinition(dataSetDescriptor, path);
        mappedDsd.setParameterizable(dsd);

        // First add in all of the report parameters
        for (Parameter reportParameter : rd.getParameters()) {
            Parameter parameter = new Parameter(reportParameter);
            mappedDsd.getParameterizable().addParameter(parameter);
            if (parameter.getDefaultValue() != null) {
                mappedDsd.addParameterMapping(parameter.getName(), parameter.getDefaultValue());
                parameter.setDefaultValue(null);
            } else {
                mappedDsd.addParameterMapping(parameter.getName(), "${" + parameter.getName() + "}");
            }
        }

        // Next, if any data set parameters specify values for report parameters, or are new parameters, add these
        List<Parameter> datasetParameters = constructParameters(dataSetDescriptor.getParameters());
        for (Parameter parameter : datasetParameters) {
            boolean found = false;
            for (Parameter existingParam : rd.getParameters()) {
                if (existingParam.getName().equals(parameter.getName())) {
                    found = true;
                    if (parameter.getDefaultValue() != null) {
                        mappedDsd.getParameterMappings().put(existingParam.getName(), parameter.getDefaultValue());
                        parameter.setDefaultValue(null);
                    }
                }
            }
            if (!found) {
                mappedDsd.getParameterizable().addParameter(parameter);
                if (parameter.getDefaultValue() != null) {
                    mappedDsd.addParameterMapping(parameter.getName(), parameter.getDefaultValue());
                    parameter.setDefaultValue(null);
                }
            }
        }

        rd.addDataSetDefinition(dataSetDescriptor.getKey(), mappedDsd);
    }

    public static List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition, ReportDescriptor reportDescriptor) {

        List<ReportDesign> reportDesigns = new ArrayList<ReportDesign>();

        // always do a default CSV design, if no designs are explicitly configured
        if (reportDescriptor.getDesigns() == null || reportDescriptor.getDesigns().size() == 0) {
            DesignDescriptor defaultDesignDescriptor = new DesignDescriptor();
            defaultDesignDescriptor.setType("csv");
            reportDescriptor.setDesigns(Collections.singletonList(defaultDesignDescriptor));
        }

        for (DesignDescriptor designDescriptor : reportDescriptor.getDesigns()) {
            ReportDesign design = null;
            if (designDescriptor.getType().equalsIgnoreCase("csv")) {
                design = constructCSVReportDesign(reportDefinition);
            }
            else if (designDescriptor.getType().equalsIgnoreCase("excel") || designDescriptor.getType().equalsIgnoreCase("xls")) {
                design = constructXlsReportDesign(reportDefinition, reportDescriptor, designDescriptor);
            }
            else {
                throw new RuntimeException("Unsupported report design type: " + designDescriptor.getType() + " for report " + reportDefinition.getName());
            }

            if (designDescriptor.getProperties() != null) {
                for (Map.Entry<String,String> property : designDescriptor.getProperties().entrySet()) {
                    design.addPropertyValue(property.getKey(), property.getValue());
                }
            }

            if (design.getPropertyValue(ReportDesignRenderer.FILENAME_BASE_PROPERTY, null) == null) {
                design.addPropertyValue(ReportDesignRenderer.FILENAME_BASE_PROPERTY, StringUtils.replace(reportDefinition.getName(), " ", ".").toLowerCase() + "." +
                        "{{ formatDate request.reportDefinition.parameterMappings.startDate \"yyyyMMdd\" }}." +
                        "{{ formatDate request.reportDefinition.parameterMappings.endDate \"yyyyMMdd\" }}." +
                        "{{ formatDate request.evaluateStartDatetime \"yyyyMMdd\" }}." +
                        "{{ formatDate request.evaluateStartDatetime \"HHmm\" }}");
            }

            if (designDescriptor.getProcessors() != null) {
                for (ProcessorDescriptor processorDescriptor : designDescriptor.getProcessors()) {
                    ReportProcessorConfiguration c = new ReportProcessorConfiguration();
                    String type = processorDescriptor.getType();
                    if ("disk".equalsIgnoreCase(type)) {
                        type = DiskReportProcessor.class.getName();
                    }
                    else if ("email".equalsIgnoreCase(type)) {
                        type = EmailReportProcessor.class.getName();
                    }
                    else if ("logging".equalsIgnoreCase(type)) {
                        type = LoggingReportProcessor.class.getName();
                    }
                    c.setProcessorType(type);
                    c.setRunOnSuccess(processorDescriptor.getRunOnSuccess());
                    c.setRunOnError(processorDescriptor.getRunOnError());
                    c.setName(processorDescriptor.getName());
                    if (processorDescriptor.getProcessorMode() != null) {
                        c.setProcessorMode(ReportProcessorConfiguration.ProcessorMode.valueOf(processorDescriptor.getProcessorMode()));
                    }
                    c.setReportDesign(design);
                    if (processorDescriptor.getConfiguration() != null) {
                        c.setConfiguration(new Properties());
                        for (Map.Entry<String,String> config : processorDescriptor.getConfiguration().entrySet()) {
                            String configValue = config.getValue();
                            configValue = configValue.replace("{{application_data_directory}}", OpenmrsUtil.getApplicationDataDirectory());
                            c.getConfiguration().setProperty(config.getKey(), configValue);
                        }
                    }
                    design.addReportProcessor(c);
                }
            }

            reportDesigns.add(design);
        }

        return reportDesigns;
    }

    public static ReportDesign constructCSVReportDesign(ReportDefinition reportDefinition) {
        ReportDesign design = new ReportDesign();
        design.setName("reporting.csv");
        design.setReportDefinition(reportDefinition);
        design.setRendererType(CsvReportRenderer.class);
        return design;
    }

    public static ReportDesign constructXlsReportDesign(ReportDefinition reportDefinition, ReportDescriptor reportDescriptor, DesignDescriptor designDescriptor) {

        ReportDesign design = new ReportDesign();
        design.setName("reporting.excel");
        design.setReportDefinition(reportDefinition);
        design.setRendererType(XlsReportRenderer.class);

        if (StringUtils.isNotBlank(designDescriptor.getTemplate())) {
            ReportDesignResource resource = new ReportDesignResource();
            resource.setName("template");
            resource.setExtension(ContentType.EXCEL.getExtension());
            resource.setContentType(ContentType.EXCEL.getContentType());

            try {
                File templateFile = new File(reportDescriptor.getPath(), designDescriptor.getTemplate());
                byte[] excelTemplate = IOUtils.toByteArray(new FileInputStream(templateFile));
                resource.setContents(excelTemplate);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load XLS template " + designDescriptor.getTemplate(), e);
            }
            resource.setReportDesign(design);
            design.addResource(resource);
        }


        return design;
    }

    public static List<ReportDescriptor> loadReportDescriptors() {
        List<ReportDescriptor> reportDescriptors = new ArrayList<ReportDescriptor>();
        Collection<File> files = null;

        try {
            File reportDir = new File(getReportingDescriptorsConfigurationDir());
            if (reportDir.exists()) {
                // search all directories and subdirectories for YAML files
                files = FileUtils.listFiles(reportDir, FileFilterUtils.suffixFileFilter("yml"), TrueFileFilter.INSTANCE);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to open reporting configuration directory", e);
        }

        if (files != null) {
            for (File file : files) {
                reportDescriptors.add(ReportLoader.load(file));
            }
        }

        return reportDescriptors;
    }

    public static ReportDescriptor load(File file) {

        ReportDescriptor reportDescriptor = null;

        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        if (file.exists()) {

            InputStream is = null;

            try {
                is = new FileInputStream(file);
                reportDescriptor = objectMapper.readValue(is, ReportDescriptor.class);
                reportDescriptor.setPath(file.getParentFile());
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to load report descriptor " + file.getAbsolutePath(), e);
            }
            finally {
                IOUtils.closeQuietly(is);
            }
        }

        return reportDescriptor;
    }
}
