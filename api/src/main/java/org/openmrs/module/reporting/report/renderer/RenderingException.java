/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.renderer;

import org.openmrs.api.APIException;

/**
 * Represents an Exception thrown during Report Rendering
 */
public class RenderingException extends APIException {
	
	public static final long serialVersionUID = 1L;
	
	/**
	 * Constructs a new Exception instance with the passed message
	 * @param message
	 */
	public RenderingException(String message) {
		super(message);
	}
	
	public RenderingException(String message, Throwable t) {
		super(message, t);
	}
}
