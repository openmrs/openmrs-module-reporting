/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting;

import org.openmrs.api.APIException;

/**
 * This is a generic reporting exception.  All reporting exceptions should 
 * derive from this class.
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
