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

/**
 * Indicates that a {@link Definition} cannot be evaluated because it refers to another definition
 * definition that is missing (perhaps deleted by a user) 
 */
public class MissingDependencyException extends EvaluationException {

	public static final long serialVersionUID = 1L;
	
	public MissingDependencyException() {
	    super(null);
    }
	
	public MissingDependencyException(String propertyThatFailed) {
	    super(propertyThatFailed);
    }
	
    public MissingDependencyException(String propertyThatFailed, Throwable cause) {
	    super(propertyThatFailed, cause);
    }

	@Override
    public String getMessage() {
		StringBuilder ret = new StringBuilder();
		ret.append("The property ");
		if (getPropertyThatFailed() != null)
			ret.append("'" + getPropertyThatFailed() + "' ");
		ret.append("cannot be found in the database. (Maybe someone deleted it?)");
		return ret.toString();
    }

}
