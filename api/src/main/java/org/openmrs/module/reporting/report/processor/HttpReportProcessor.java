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

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.report.Report;
import org.springframework.stereotype.Component;

/**
* A ReportProcessor which sends the rendered report via http POST
*/

@Component
public class HttpReportProcessor implements ReportProcessor {

	protected Log log = LogFactory.getLog(this.getClass());
	
	public static final String CONNECTION_URL = "connectionUrl";
	public static final String SUBJECT = "subject";
	public static final String ADD_REPORT = "addReport";
	
	/**
	 * @see ReportProcessor#getConfigurationPropertyNames()
	 */
	@Override
	public List<String> getConfigurationPropertyNames() {
		List<String> ret = new ArrayList<String>();
		ret.add(CONNECTION_URL);
		ret.add(SUBJECT);
		ret.add(ADD_REPORT );
		return ret;
	}

	/**
	 * Performs some action on the given report
	 * 
	 * @param report the Report to process
	 */
	@Override
	public void process(Report report, Properties configuration) {
		OutputStreamWriter writer = null;

		try {
			if (report.getRenderedOutput() != null && "true".equalsIgnoreCase(configuration.getProperty(ADD_REPORT))) {
			URL url = new URL(configuration.getProperty(CONNECTION_URL));
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			String outPutContentType = report.getOutputContentType();
			connection.setRequestProperty("Content-Type", "outPutContentType; charset=UTF-8");
			connection.setDoOutput(true);
			connection.connect();
			 //when the parameter ADD_REPORT is set to true then the rendered report is added to the url connection 
			//to be written with the respective SUBJECT
				String addContent = configuration.getProperty(SUBJECT,"");
				addContent =  new String(report.getRenderedOutput(),"UTF-8");	
				writer = new OutputStreamWriter(
					    connection.getOutputStream());
				writer.write(addContent);
				writer.flush();
				}
			
		} catch (Exception e) {
			throw new RuntimeException("Error occurred while sending report via http POST", e);
		}
		finally {
			IOUtils.closeQuietly(writer);
		}

	}

}
