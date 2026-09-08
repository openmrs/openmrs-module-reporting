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
