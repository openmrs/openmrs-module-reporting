package org.openmrs.module.reporting.report.processor;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.report.Report;
import org.springframework.stereotype.Component;

/**
 * A ReportProcessor which sends the rendered report via HTTP POST
 */
@Component
public class HttpReportProcessor implements ReportProcessor {

    protected Log log = LogFactory.getLog(this.getClass());

    /**
     * @see ReportProcessor#getConfigurationPropertyNames()
     */
    public List<String> getConfigurationPropertyNames() {
        List<String> ret = new ArrayList<String>();
        ret.add("url");
        ret.add("contentType");
        ret.add("dateFrom");
        ret.add("dateTo");
        return ret;
    }

    /**
     * Performs some action on the given report
     * @param report the Report to process
     */
    public void process(Report report, Properties configuration) {

        try {
            String urlString = configuration.getProperty("url");
            if (StringUtils.isBlank(urlString)) {
                throw new IllegalArgumentException("URL cannot be blank");
            }

            String contentType = configuration.getProperty("contentType", "text/plain");
            String dateFrom = configuration.getProperty("dateFrom");
            String dateTo = configuration.getProperty("dateTo");

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", contentType);

            byte[] reportData = report.getRenderedOutput();

            conn.setRequestProperty("Date-From", dateFrom);
            conn.setRequestProperty("Date-To", dateTo);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(reportData);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                log.info("Report sent successfully to " + urlString);
            } else {
                log.error("Failed to send report to " + urlString + ", HTTP error code: " + responseCode);
            }

            conn.disconnect();
        }
        catch (Exception e) {
            throw new RuntimeException("Error occurred while sending report via HTTP", e);
        }
    }
}
