package org.openmrs.module.reporting.report.processor;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.openmrs.module.reporting.report.Report;

public class HttpReportProcessor implements ReportProcessor {
    private String connectionUrl;


    public HttpReportProcessor(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    @Override
    public List<String> getConfigurationPropertyNames() {
        List<String> ret = new ArrayList<>();
        ret.add("connectionUrl");
        ret.add("exportType");
        ret.add("reportName");
        ret.add("description");
        ret.add("dateFrom");
        ret.add("dateTo");
        return ret;
    }


    @Override
    public void process(Report report, Properties configuration) {
        this.connectionUrl = configuration.getProperty("connectionUrl");

        try {
            // Establish connection to the URL
            URL url = new URL(connectionUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Write report data to the request body
            String reportData = generateReportData(report, configuration);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = reportData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Check response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Connection is successful
                System.out.println("Report sent successfully via HTTP POST.");
            } else {
                // Handle other response codes if needed
                System.out.println("Failed to send report. Response code: " + responseCode);
            }
        } catch (Exception e) {
            // Handle connection errors
            System.out.println("Error sending report via HTTP POST: " + e.getMessage());
        }
    }

    private String generateReportData(Report report, Properties configuration) {
        // Example method to generate report data
        String exportType = configuration.getProperty("exportType");
        String reportName = configuration.getProperty("reportName");
        String description = configuration.getProperty("description");
        String dateFrom = configuration.getProperty("dateFrom");
        String dateTo = configuration.getProperty("dateTo");

        // Construct JSON object with report properties
        JSONObject data = new JSONObject();
         data.put("exportType", exportType);
        data.put("reportName", reportName);
        data.put("description", description);
        data.put("dateFrom", dateFrom);
        data.put("dateTo", dateTo);

        // Replace this with your actual report data if needed
        data.put("reportData", "Sample report data");

        return data.toString();
    }
}
