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

import org.apache.commons.lang.exception.ExceptionUtils;
import org.openmrs.api.APIAuthenticationException;


/**
 * Utility methods for dealing with exceptions
 */
public class ExceptionUtil {
	
	/**
	 * If any cause in the exception chain is an instance of causeType, then re-throw that exception 
	 * 
	 * @param thrown
	 * @param causeType
	 */
	public static void rethrowIfCause(Throwable thrown, Class<? extends RuntimeException> causeType) {
		int index = ExceptionUtils.indexOfType(thrown, causeType);
		if (index >= 0)
			throw (RuntimeException) ExceptionUtils.getThrowables(thrown)[index];
	}
	
	/**
	 * If any cause in the given exception chain is an APIAuthenticationException, re-throw that 
	 * 
	 * @param thrown
	 */
	public static void rethrowAuthenticationException(Throwable thrown) {
		rethrowIfCause(thrown, APIAuthenticationException.class);
	}
	
}
