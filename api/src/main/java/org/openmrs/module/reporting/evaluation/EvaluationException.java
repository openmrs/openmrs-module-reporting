package org.openmrs.module.reporting.evaluation;

import org.openmrs.api.APIException;

/**
 * Indicates that evaluating a {@link Definition} failed.
 * 
 * Note that this is a *checked* exception, which differs from most OpenMRS exceptions. We do this because
 * there's no other way to reliably propagate error message for exceptions in nested evaluations.
 */
public class EvaluationException extends Exception {

    public static final long serialVersionUID = 1L;
    
    private String propertyThatFailed;
        
	public EvaluationException(String propertyThatFailed) {
		super();
	    this.propertyThatFailed = propertyThatFailed;
    }
    
    public EvaluationException(String propertyThatFailed, Throwable cause) {
    	super(cause);
    	this.propertyThatFailed = propertyThatFailed;
    }

	@Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Failed to evaluate");
        if (propertyThatFailed != null)
        	sb.append(" " + propertyThatFailed);
        if (getCause() != null)
        	sb.append(" because:\n" + getCause().getMessage());
        return sb.toString();
    }

    /**
     * @return the propertyThatFailed
     */
    public String getPropertyThatFailed() {
    	return propertyThatFailed;
    }

	
    /**
     * @param propertyThatFailed the propertyThatFailed to set
     */
    public void setPropertyThatFailed(String propertyThatFailed) {
    	this.propertyThatFailed = propertyThatFailed;
    }

}
