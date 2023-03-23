/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.reporting.report.processor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openmrs.module.reporting.report.Report;
import org.springframework.stereotype.Component;

/**
 * A ReportProcessor which sends the rendered report via POST
 */
@Component
public class HttpReportProcessor implements ReportProcessor {

    public final String CONNECTION_URL = "url";
    public final String SUBJECT = "subject";
    public final String ADD_REPORT = "addReport";

    /**
     * @see ReportProcessor#getConfigurationPropertyNames()
     */
    @Override
    public List<String> getConfigurationPropertyNames() {

        List<String> ret = new ArrayList<String>();
        ret.add(CONNECTION_URL);
        ret.add(SUBJECT);
        ret.add(ADD_REPORT);
        return ret;
    }

    /**
     * Performs some action on the given report
     * @param report the Report to process
     */
    @Override
    public void process(Report report, Properties configuration) {
        // TODO Auto-generated method stub
        HttpURLConnection connection = null;
         try {
             if (report.getRenderedOutput() != null && "true".equalsIgnoreCase(configuration.getProperty(SUBJECT))) {

                 URL url = new URL(configuration.getProperty(CONNECTION_URL));

                 connection = (HttpURLConnection) url.openConnection();

                 connection.setRequestMethod("POST");

                 connection.setRequestProperty("Content-Type", "application/json");

                 connection.setDoOutput(true);

                 String reportData = configuration.getProperty(ADD_REPORT, "");
                 reportData += new String(report.getRenderedOutput());

                 DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                 outputStream.writeBytes(reportData);
                 outputStream.flush();
                 outputStream.close();

                 int responseCode = connection.getResponseCode();
                 if (responseCode == HttpURLConnection.HTTP_OK) {

                     BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                     String responseData;
                     while ((responseData = reader.readLine()) != null) {
                         System.out.println(responseData);
                     }
                     reader.close();
                 } else {
                     System.out.println("HTTP POST request failed with response code: " + responseCode);
                 }

             }

         } catch(IOException e) {
             e.getStackTrace();
         } finally {
            if(connection != null) {
                connection.disconnect();
            }
         }
    }

}
