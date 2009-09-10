package org.openmrs.module.report;

import java.util.Date;


/**
 * This represents the result of having run a {@link ReportRequest}.
 * It includes the run request, ReportData, possibly the result of rendering (if a non-web
 * renderer was selected), and timestamp information about the run.
 */
public class Report {
	
	private ReportRequest request;
	private Date evaluateStartDate;
	private Date evaluateCompleteDate;
	private Date renderCompleteDate;
	private ReportData rawData;
	// TODO maybe switch byte[] to a file instead
	private byte[] renderedOutput;
	private String renderedFilename;
	private String renderedContentType;
	
	public Report() {
	}
	
	
	public Report(ReportRequest request) {
	    this.request = request;
    }
	
	
	public void startEvaluating() {
		evaluateStartDate = new Date();
	}
	
	
	public void rawDataEvaluated(ReportData rawData) {
		this.rawData = rawData;
		this.evaluateCompleteDate = new Date();
	}

	
	public void outputRendered(String filename, String contentType, byte[] renderedOutput) {
		this.renderedFilename = filename;
		this.renderedContentType = contentType;
		this.renderedOutput = renderedOutput;
		this.renderCompleteDate = new Date();
	}
	
	
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
     * @return the evaluateStartDate
     */
    public Date getRunStartDate() {
    	return evaluateStartDate;
    }

	
    /**
     * @param evaluateStartDate the evaluateStartDate to set
     */
    public void setRunStartDate(Date runStartDate) {
    	this.evaluateStartDate = runStartDate;
    }
	
	
    /**
     * @return the evaluateCompleteDate
     */
    public Date getEvaluateCompleteDate() {
    	return evaluateCompleteDate;
    }

	
    /**
     * @param evaluateCompleteDate the evaluateCompleteDate to set
     */
    public void setEvaluateCompleteDate(Date evaluateCompleteDate) {
    	this.evaluateCompleteDate = evaluateCompleteDate;
    }

    
	/**
     * @return the rawData
     */
    public ReportData getRawData() {
    	return rawData;
    }

	
    /**
     * @param rawData the rawData to set
     */
    public void setRawData(ReportData rawData) {
    	this.rawData = rawData;
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
     * @return the evaluateStartDate
     */
    public Date getEvaluateStartDate() {
    	return evaluateStartDate;
    }

	
    /**
     * @param evaluateStartDate the evaluateStartDate to set
     */
    public void setEvaluateStartDate(Date evaluateStartDate) {
    	this.evaluateStartDate = evaluateStartDate;
    }

	
    /**
     * @return the renderCompleteDate
     */
    public Date getRenderCompleteDate() {
    	return renderCompleteDate;
    }


    /**
     * @param renderCompleteDate the renderCompleteDate to set
     */
    public void setRenderCompleteDate(Date renderCompleteDate) {
    	this.renderCompleteDate = renderCompleteDate;
    }

	
    /**
     * @return the renderedFilename
     */
    public String getRenderedFilename() {
    	return renderedFilename;
    }

	
    /**
     * @param renderedFilename the renderedFilename to set
     */
    public void setRenderedFilename(String renderedFilename) {
    	this.renderedFilename = renderedFilename;
    }


    /**
     * @return the renderedContentType
     */
    public String getRenderedContentType() {
    	return renderedContentType;
    }


    /**
     * @param renderedContentType the renderedContentType to set
     */
    public void setRenderedContentType(String renderedContentType) {
    	this.renderedContentType = renderedContentType;
    }
	
}
