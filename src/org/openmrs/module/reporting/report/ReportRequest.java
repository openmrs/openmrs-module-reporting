package org.openmrs.module.reporting.report;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.User;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
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
	private Mapped<CohortDefinition> baseCohort; //optional
	private Mapped<ReportDefinition> reportDefinition;
	private RenderingMode renderingMode;
	private User requestedBy;
	private Date requestDate;
	private boolean saved = false;
	private List<String> labels;
	private Priority priority = Priority.NORMAL;
	
	// once the request is completed keep a pointer to the resulting file (unless a WebRenderer was chosen)
	private File renderedOutput;
	
	
	public ReportRequest() {
	}

	
	public ReportRequest(Mapped<ReportDefinition> reportDefinition, Mapped<CohortDefinition> baseCohort,
	                     RenderingMode renderingMode, Priority priority) {
	    super();
	    this.reportDefinition = reportDefinition;
	    this.baseCohort = baseCohort;
	    this.renderingMode = renderingMode;
	    this.priority = priority;
    }
	

	public void addLabel(String label) {
		if (labels == null) {
			labels = new ArrayList<String>();
		}
		if (!labels.contains(label)) {
			labels.add(label);
		}
	}
	
	
	public void removeLabel(String label) {
		labels.remove(label);
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
    public Mapped<ReportDefinition> getReportDefinition() {
    	return reportDefinition;
    }

	
    /**
     * @param reportDefinition the reportDefinition to set
     */
    public void setReportDefinition(Mapped<ReportDefinition> reportDefinition) {
    	this.reportDefinition = reportDefinition;
    }

	
    /**
     * @return the renderingMode
     */
    public RenderingMode getRenderingMode() {
    	return renderingMode;
    }

	
    /**
     * @param renderingMode the renderingMode to set
     */
    public void setRenderingMode(RenderingMode renderingMode) {
    	this.renderingMode = renderingMode;
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

	
    /**
     * @return the baseCohort
     */
    public Mapped<CohortDefinition> getBaseCohort() {
    	return baseCohort;
    }

	
    /**
     * @param baseCohort the baseCohort to set
     */
    public void setBaseCohort(Mapped<CohortDefinition> baseCohort) {
    	this.baseCohort = baseCohort;
    }

	
    /**
     * @return the saved
     */
    public boolean isSaved() {
    	return saved;
    }

	
    /**
     * @param saved the saved to set
     */
    public void setSaved(boolean saved) {
    	this.saved = saved;
    }

	
    /**
     * @return the labels
     */
    public List<String> getLabels() {
    	return labels;
    }

	
    /**
     * @param labels the labels to set
     */
    public void setLabels(List<String> labels) {
    	this.labels = labels;
    }

	
    /**
     * @return the renderedOutput
     */
    public File getRenderedOutput() {
    	return renderedOutput;
    }

	
    /**
     * @param renderedOutput the renderedOutput to set
     */
    public void setRenderedOutput(File renderedOutput) {
    	this.renderedOutput = renderedOutput;
    }

}
