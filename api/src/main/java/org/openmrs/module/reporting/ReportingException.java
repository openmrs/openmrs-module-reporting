/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting;

import org.openmrs.api.APIException;

/**
 * This is a generic reporting exception.  All reporting exceptions should be
 * derived from this class.
 */
public class ReportingException extends APIException {
	
	public static final long serialVersionUID = 1L;
	
	/**
	 * @see {@link #APIException(String)}
	 */
	public ReportingException(String message) {
		super(message);
	}
	
	/**
	 * @see {@link #APIException(String, Throwable)}
	 */
	public ReportingException(String message, Throwable throwable) { 
		super(message, throwable);
		
	}
	
}
