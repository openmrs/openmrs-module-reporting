package org.openmrs.module.report;

import java.util.Date;
import java.util.Map;

import org.openmrs.User;
import org.openmrs.module.report.renderer.ReportRenderer;
import org.openmrs.util.OpenmrsUtil;


/**
 * Represents a request to run and render a report.
 * 
 * The uuid in this request will be populated when you hand it to the ReportService to run.
 * 
 * (Note that the natural ordering of this class places higher priority requests first, so that it may
 * be used in a PriorityQueue.) 
 */
public class ReportRequest implements Comparable<ReportRequest> {
	
	/**
	 * Priority with which to run these reports. Generally speaking
	 * <ul>
	 * <li>a request that a User initiates that expects an interactive result (e.g. a WebRenderer)
	 * should get HIGHEST priority</li>
	 * <li>a request that a User initiates that expects a file download result should get HIGH
	 * priority</li>
	 * <li>a request created by a demon thread should have LOW or LOWEST priority.</li>
	 * </ul>
	 */
	public enum Priority {
		HIGHEST,
		HIGH,
		NORMAL,
		LOW,
		LOWEST
	}

	private String uuid;
	private ReportDefinition reportDefinition;
	private Map<String, Object> parameterValues;
	private ReportRenderer reportRenderer;
	private String renderArgument;
	private User requestedBy;
	private Date requestDate;
	private Priority priority = Priority.NORMAL;
	
	public ReportRequest() {
	}

	public ReportRequest(ReportDefinition reportDefinition, Map<String, Object> parameterValues,
        ReportRenderer reportRenderer, String renderArgument, Priority priority) {
	    super();
	    this.reportDefinition = reportDefinition;
	    this.parameterValues = parameterValues;
	    this.reportRenderer = reportRenderer;
	    this.renderArgument = renderArgument;
	    this.priority = priority;
    }
	
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		ReportRequest other = (ReportRequest) o;
		if (uuid == null || other.uuid == null) {
			return this == other;
		} else {
			return uuid.equals(other.uuid);
		}
	}

	
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * 
	 * @should compare by priority
	 * @should compare by request date when priority is the same
	 */
	public int compareTo(ReportRequest other) {
	    int temp = priority.compareTo(other.priority);
	    if (temp == 0)
	    	temp = OpenmrsUtil.compareWithNullAsLatest(requestDate, other.requestDate);
	    return temp;
    }
	
	
	/**
     * @return the reportDefinition
     */
    public ReportDefinition getReportDefinition() {
    	return reportDefinition;
    }

	
    /**
     * @param reportDefinition the reportDefinition to set
     */
    public void setReportDefinition(ReportDefinition reportDefinition) {
    	this.reportDefinition = reportDefinition;
    }

	
    /**
     * @return the parameterValues
     */
    public Map<String, Object> getParameterValues() {
    	return parameterValues;
    }

	
    /**
     * @param parameterValues the parameterValues to set
     */
    public void setParameterValues(Map<String, Object> parameterValues) {
    	this.parameterValues = parameterValues;
    }

	
    /**
     * @return the reportRenderer
     */
    public ReportRenderer getReportRenderer() {
    	return reportRenderer;
    }

	
    /**
     * @param reportRenderer the reportRenderer to set
     */
    public void setReportRenderer(ReportRenderer reportRenderer) {
    	this.reportRenderer = reportRenderer;
    }

	
    /**
     * @return the renderArgument
     */
    public String getRenderArgument() {
    	return renderArgument;
    }

	
    /**
     * @param renderArgument the renderArgument to set
     */
    public void setRenderArgument(String renderArgument) {
    	this.renderArgument = renderArgument;
    }

	
    /**
     * @return the requestedBy
     */
    public User getRequestedBy() {
    	return requestedBy;
    }

	
    /**
     * @param requestedBy the requestedBy to set
     */
    public void setRequestedBy(User requestedBy) {
    	this.requestedBy = requestedBy;
    }

	
    /**
     * @return the requestDate
     */
    public Date getRequestDate() {
    	return requestDate;
    }

	
    /**
     * @param requestDate the requestDate to set
     */
    public void setRequestDate(Date requestDate) {
    	this.requestDate = requestDate;
    }

	
    /**
     * @return the priority
     */
    public Priority getPriority() {
    	return priority;
    }

	
    /**
     * @param priority the priority to set
     */
    public void setPriority(Priority priority) {
    	this.priority = priority;
    }

	
    /**
     * @return the uuid
     */
    public String getUuid() {
    	return uuid;
    }

	
    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String requestUuid) {
    	this.uuid = requestUuid;
    }

}
