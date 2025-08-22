package org.openmrs.module.reporting.report.processor;

import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportProcessor;
import org.openmrs.module.reporting.report.ReportProcessorConfiguration;
import org.openmrs.module.reporting.report.ReportRequest.Status;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

/**
 * HTTP Report Processor that sends reports via HTTP POST to a configured URL.
 */
public class HttpReportProcessor implements ReportProcessor {

    @Override
    public void process(ReportRequest request) {
        // Get the report contents
        String reportContent = request.getReportDefinition().toString(); // Convert report to JSON or XML

        // Get the configured URL from processor config
        ReportProcessorConfiguration config = request.getReportProcessorConfiguration();
        String url = config.getConfigurationProperty("httpUrl");

        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("HTTP URL must be configured for HttpReportProcessor.");
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(reportContent));

            CloseableHttpResponse response = httpClient.execute(post);
            response.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to send report via HTTP POST", e);
        }

        // Mark report as processed
        request.setStatus(Status.COMPLETED);
    }
}
