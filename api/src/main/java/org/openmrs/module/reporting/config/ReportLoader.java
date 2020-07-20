package org.openmrs.module.reporting.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReportLoader {

    private static Log log = LogFactory.getLog(ReportLoader.class);

    public static final String getReportingDescriptorsConfigurationDir() {
        return OpenmrsUtil.getApplicationDataDirectory() +  File.separator + "configuration" +  File.separator + "reporting" +  File.separator + "reportdescriptors";
    }

    public static List<ReportDescriptor> loadReportDescriptors() {
        List<ReportDescriptor> reportDescriptors = new ArrayList<ReportDescriptor>();
        Collection<File> files = null;

        try {
            File reportDir = new File(getReportingDescriptorsConfigurationDir());
            // search all directories and subdirectories for YAML files
            files = FileUtils.listFiles(reportDir, FileFilterUtils.suffixFileFilter("yml"), TrueFileFilter.INSTANCE);
        }
        catch (Exception e) {
            log.error("Unable to open reporting configuration diretory");
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
