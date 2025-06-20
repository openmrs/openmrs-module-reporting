/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report;

import org.openmrs.module.reporting.report.renderer.ReportRenderer;

/**
 * This represents the result of having run a {@link ReportRequest}.
 * It includes the request, and any resulting data and output from the evaluation.
 */
public class Report {
	
	//***** PROPERTIES *****
	
	private ReportRequest request;
	private ReportData reportData;
	private byte[] renderedOutput;
	private String errorMessage;

	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public Report() {}
	
	/**
	 * Default Constructor
	 */
	public Report(ReportRequest request) {
	    this.request = request;
    }
	
	//***** CONTENT TYPES *****
	
	/**
	 * @return the rendered content type for the given Report
	 */
	public String getOutputContentType() {
		ReportRenderer renderer = request.getRenderingMode().getRenderer();
		return renderer.getRenderedContentType(request);
	}
	
	//***** PROPERTY ACCESS *****
		
    /**
     * @return the request
     */
    public ReportRequest getRequest() {
    	return request;
    }
	
    /**
     * @param request the request to set
     */
    public void setRequest(ReportRequest request) {
    	this.request = request;
    }

	/**
	 * @return the reportData
	 */
	public ReportData getReportData() {
		return reportData;
	}

	/**
	 * @param reportData the reportData to set
	 */
	public void setReportData(ReportData reportData) {
		this.reportData = reportData;
	}

	/**
	 * @return the renderedOutput
	 */
	public byte[] getRenderedOutput() {
		return renderedOutput;
	}

	/**
	 * @param renderedOutput the renderedOutput to set
	 */
	public void setRenderedOutput(byte[] renderedOutput) {
		this.renderedOutput = renderedOutput;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
