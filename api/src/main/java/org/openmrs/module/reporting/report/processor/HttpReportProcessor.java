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


import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.report.Report;
import org.springframework.stereotype.Component;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/*
A ReportProcessor which sends the rendered report via POST
 */
@Component
public class HttpReportProcessor implements ReportProcessor{
	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * @see ReportProcessor#getConfigurationPropertyNames()
	 */

	@Override
	public List<String> getConfigurationPropertyNames() {
		List<String> ret = new ArrayList<String>();
		ret.add("connectionUrl");
		ret.add("subject");
		ret.add("addReport");
		return ret;
	}

	/**
	 * Performs some action on the given report
	 * @param report the Report to process
	 */

	@Override
	public void process(Report report, Properties configuration) {
		HttpURLConnection connection;
		OutputStreamWriter out = null;

		try {
			if (report.getRenderedOutput() != null && "true".equalsIgnoreCase(configuration.getProperty("addReport"))) {
				URL url = new URL(configuration.getProperty("connectionUrl"));
				connection = (HttpURLConnection) url.openConnection();
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", report.getOutputContentType());
				out = new OutputStreamWriter(connection.getOutputStream());
				out.write(configuration.getProperty("subject"));
				out.close();
			}

		} catch (Exception e) {
			throw new RuntimeException("Error occurred while sending report via http POST", e);
		}
		finally {
			IOUtils.closeQuietly(out);
		}

	}


}
