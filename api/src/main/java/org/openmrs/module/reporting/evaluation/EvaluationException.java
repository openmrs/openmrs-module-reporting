/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
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
