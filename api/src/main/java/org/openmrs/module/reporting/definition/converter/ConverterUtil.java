/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.definition.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.definition.service.SerializedDefinitionService;

/**
 * Utility class for managing conversion
 */
public class ConverterUtil {
	
	protected static final Log log = LogFactory.getLog(ConverterUtil.class);
	
	private static Integer numInvalidDefinitions = null;
	
	/**
	 * @return true if conversion is needed
	 */
	public static synchronized boolean isConversionNeeded() {
		return getNumberNeedingConversion() > 0;
	}
	
	/**
	 * @return the number of invalid Definitions that require conversion
	 */
	public static synchronized int getNumberNeedingConversion() {
		if (numInvalidDefinitions == null) {
			refreshConversionStatus();
		}
		return numInvalidDefinitions;
	}
	
	/**
	 * Updates the number of invalid definitions from a query of the database and caches the result
	 */
	public static synchronized void refreshConversionStatus() {
		numInvalidDefinitions = Context.getService(SerializedDefinitionService.class).getInvalidDefinitions(true).size();
		log.warn("Refresh conversion status returned " + numInvalidDefinitions + " definitions that require conversion.");
	}
}
