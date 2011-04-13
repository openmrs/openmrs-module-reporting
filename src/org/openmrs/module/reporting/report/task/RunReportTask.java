package org.openmrs.module.reporting.report.task;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Priority;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.serializer.ReportingSerializer;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.serialization.SerializationException;

/**
 * A scheduled task that runs a report, with user-provided parameters 
 */
public class RunReportTask extends AbstractTask {

	/**
	 * @return the {@link TaskDefinition} backing this task
	 */
	public TaskDefinition getTaskDefinition() {
		if (taskDefinition.getName() == null && getReportDefinition() != null)
			taskDefinition.setName(getDefaultTaskName());
		taskDefinition.setTaskClass(getClass().getName());
		return taskDefinition;
	}

	/**
	 * TODO include the parameters in here somehow
	 * @return a default name for this task
	 */
	private String getDefaultTaskName() {
		return "Run " + getReportDefinition().getParameterizable().getName() + " Task";
    }

    /**
     * @see org.openmrs.scheduler.tasks.AbstractTask#initialize(org.openmrs.scheduler.TaskDefinition)
     */
    @Override
	public void initialize(TaskDefinition taskDefinition) {
		this.taskDefinition = taskDefinition;
	}
	
	/**
	 * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
	 */
	@Override
	public void execute() {
		if (!isExecuting()) {
			isExecuting = true;
			try {
				Context.openSession();
				if (!Context.isAuthenticated()) {
					authenticate();
				}

				ReportRequest request = new ReportRequest(getReportDefinition(), getBaseCohort(), getRenderingMode(), getPriority());
				Context.getService(ReportService.class).queueReport(request);
			} finally {
				if (Context.isSessionOpen())
					Context.closeSession();
				isExecuting = false;
			}
		}
	}

	
    /**
     * Convenience method to get a property that has been stored in serialized form in the underlying
     * {@link TaskDefinition}
     * @param property
     * @return
     */
    private Object getSerializedProperty(String property) {
    	String serialized = taskDefinition.getProperty(property);
    	if (StringUtils.isEmpty(serialized))
    		return null;
    	try {
	        return Context.getSerializationService().getSerializer(ReportingSerializer.class)
	        	.deserialize(serialized, Mapped.class);
        } catch (SerializationException ex) {
        	throw new APIException("Cannot load " + property, ex);
        }
    }

    /**
     * Convenience method to store a serialized version of the given property in the underlying
     * {@link TaskDefinition}
     * @param property
     * @param value
     */
    private void setSerializedProperty(String property, Object value) {
    	if (value == null || "".equals(value)) {
    		taskDefinition.setProperty(property, null);
    		return;
    	}
    	try {
	        taskDefinition.setProperty(property, Context.getSerializationService().serialize(value, ReportingSerializer.class));
        } catch (SerializationException ex) {
        	throw new APIException("Cannot serialize " + property, ex);
        }
    }

	
    /**
     * @return the reportDefinition
     */
    @SuppressWarnings("unchecked")
    public Mapped<ReportDefinition> getReportDefinition() {
    	return (Mapped<ReportDefinition>) getSerializedProperty("reportDefinition");
    }
    

    /**
     * @param reportDefinition the reportDefinition to set
     */
    public void setReportDefinition(Mapped<ReportDefinition> reportDefinition) {
    	setSerializedProperty("reportDefinition", reportDefinition);
    }
    
	
    /**
     * @return the baseCohort
     */
    @SuppressWarnings("unchecked")
    public Mapped<CohortDefinition> getBaseCohort() {
    	return (Mapped<CohortDefinition>) getSerializedProperty("baseCohort");
    }

	
    /**
     * @param baseCohort the baseCohort to set
     */
    public void setBaseCohort(Mapped<CohortDefinition> baseCohort) {
    	setSerializedProperty("baseCohort", baseCohort);
    }

	
    /**
     * @return the renderingMode
     */
    public RenderingMode getRenderingMode() {
    	return (RenderingMode) getSerializedProperty("renderingMode");
    }

	
    /**
     * @param renderingMode the renderingMode to set
     */
    public void setRenderingMode(RenderingMode renderingMode) {
    	setSerializedProperty("renderingMode", renderingMode);
    }

	
    /**
     * @return the priority
     */
    public ReportRequest.Priority getPriority() {
    	return (Priority) getSerializedProperty("priority");
    }

	
    /**
     * @param priority the priority to set
     */
    public void setPriority(ReportRequest.Priority priority) {
    	setSerializedProperty("priority", priority);
    }
    
    
    /**
     * @return the startTime
     */
    public Date getStartTime() {
    	return taskDefinition.getStartTime();
    }
    
    /**
     * @param startTime
     */
    public void setStartTime(Date startTime) {
    	taskDefinition.setStartTime(startTime);
    }
    
    /**
     * @return the repeatInterval
     */
    public Long getRepeatInterval() {
    	return taskDefinition.getRepeatInterval();
    }
	
    /**
     * @param repeatInterval
     */
    public void setRepeatInterval(Long repeatInterval) {
    	taskDefinition.setRepeatInterval(repeatInterval);
    }
    
    /**
     * @return the id of the underlying {@link TaskDefinition}
     */
    public Integer getTaskDefinitionId() {
    	return taskDefinition.getId();
    }
	
}
