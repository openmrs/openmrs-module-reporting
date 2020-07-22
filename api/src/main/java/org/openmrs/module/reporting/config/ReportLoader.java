package org.openmrs.module.reporting.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.api.db.SerializedObjectDAO;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlFileDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.report.renderer.ReportDesignRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportLoader {

    private static Log log = LogFactory.getLog(ReportLoader.class);

    public static final String getReportingDescriptorsConfigurationDir() {
        return OpenmrsUtil.getApplicationDataDirectory() +  File.separator + "configuration" +  File.separator + "reports" +  File.separator + "reportdescriptors";
    }

    public static void loadReportsFromConfig() {
        for (ReportDescriptor reportDescriptor : loadReportDescriptors()) {
            ReportDefinition reportDefinition = constructReportDefinition(reportDescriptor);
            saveReportDefinition(reportDefinition);
            List<ReportDesign> reportDesigns = constructReportDesigns(reportDefinition);
            saveReportDesigns(reportDefinition, reportDesigns);
        }
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
        if (existingDesigns.size() > 0) {
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
        // TODO do we need to handle translations here?
        rd.setName(reportDescriptor.getName());
        rd.setDescription(reportDescriptor.getDescription());
        rd.setUuid(reportDescriptor.getUuid());

        List<Parameter> parameters = constructParameters(reportDescriptor.getParameters());
        Map<String, Object> mappings = constructMappings(parameters);
        rd.setParameters(parameters);

        if (reportDescriptor.getDatasets() != null) {
            for (DataSetDescriptor dataSetDescriptor : reportDescriptor.getDatasets()) {
                DataSetDefinition dsd = constructDataSetDefinition(dataSetDescriptor, parameters);
                // TODO do we need to handle DSD name and description?
                if (dsd != null) {
                    rd.addDataSetDefinition(dataSetDescriptor.getKey(), dsd, mappings);
                }
            }
        }
        return rd;
    }

    public static List<Parameter> constructParameters(List<ParameterDescriptor> parameterDescriptors) {
        List<Parameter> parameters = new ArrayList<Parameter>();
        if (parameterDescriptors != null) {
            for (ParameterDescriptor parameterDescriptor : parameterDescriptors) {
                try {
                    // TODO how else might we want to configure parameters? ie other "types" besides classes?
                    parameters.add(new Parameter(parameterDescriptor.getKey(), parameterDescriptor.getLabel(), Class.forName(parameterDescriptor.getType())));
                } catch (Exception e) {
                    log.error("Unable to configure parameter " + parameterDescriptor.getKey(), e);
                }
            }
        }
        return parameters;
    }


    public static Map<String, Object> constructMappings(List<Parameter> parameters) {
        // TODO: Mike is the correct and/or necessary?
        Map<String,Object> mappings = new HashMap<String, Object>();
        for (Parameter parameter : parameters) {
            mappings.put(parameter.getName(), "${" + parameter.getName() + "}");
        }
        return mappings;
    }

    public static DataSetDefinition constructDataSetDefinition(DataSetDescriptor dataSetDescriptor, List<Parameter> parameters) {

        DataSetDefinition dsd = null;

        if ("sql".equalsIgnoreCase(dataSetDescriptor.getType())) {
            dsd = constructSQLFileDataSetDefinition(dataSetDescriptor);
        }
        else {
            log.error("Unsupported data set descriptor type: " + dataSetDescriptor.getType());
        }

        if (parameters != null) {
            for (Parameter parameter : parameters) {
                dsd.addParameter(parameter);
            }
        }

        return dsd;
    }

    public static SqlFileDataSetDefinition constructSQLFileDataSetDefinition(DataSetDescriptor dataSetDescriptor) {

        SqlFileDataSetDefinition dsd = new SqlFileDataSetDefinition();

        File sqlFile = new File(getReportingDescriptorsConfigurationDir() + File.separator + dataSetDescriptor.getConfig());

        if (sqlFile.exists()) {
            dsd.setSqlFile(sqlFile.getAbsolutePath());
        }
        else {
            log.error("SQL file " + dataSetDescriptor.getConfig() + " not found");
        }

        return dsd;
    }

    public static List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {

        // currently only support a default, single CSV report design
        ReportDesign csvDesign = constructCSVReportDesign(reportDefinition);

        // TODO: more robust whitespace removal?  how do we want to determine the filename?
        csvDesign.addPropertyValue(ReportDesignRenderer.FILENAME_BASE_PROPERTY, StringUtils.replace(reportDefinition.getName(), " ", ".").toLowerCase() + "." +
                "{{ formatDate request.reportDefinition.parameterMappings.startDate \"yyyyMMdd\" }}." +
                "{{ formatDate request.reportDefinition.parameterMappings.endDate \"yyyyMMdd\" }}." +
                "{{ formatDate request.evaluateStartDatetime \"yyyyMMdd\" }}." +
                "{{ formatDate request.evaluateStartDatetime \"HHmm\" }}");

        return Collections.singletonList(csvDesign);
    }

    public static ReportDesign constructCSVReportDesign(ReportDefinition reportDefinition) {
        ReportDesign design = new ReportDesign();
        design.setName(reportDefinition.getName() + ".csv");  // TODO: is this what we want for a name? or this supported to just be a message code for "CSV"?
        design.setReportDefinition(reportDefinition);
        design.setRendererType(CsvReportRenderer.class);

        // TODO: should we parameterize these? this was stuff I just copied from Mirebaials... are we okay with the default being ISO-8859-1; not sure what the blacklist does
        design.addPropertyValue("blacklistRegex", "[^\\p{InBasicLatin}\\p{L}]");
        design.addPropertyValue("characterEncoding", "ISO-8859-1");
        design.addPropertyValue("dateFormat", "dd-MMM-yyyy HH:mm:ss");

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
            log.error("Unable to open reporting configuration directory", e);
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
            }
            catch (Exception e) {
                log.error("Unable to load report descriptor " + file.getAbsolutePath(), e);
            }
            finally {
                IOUtils.closeQuietly(is);
            }
        }

        return reportDescriptor;
    }
}
