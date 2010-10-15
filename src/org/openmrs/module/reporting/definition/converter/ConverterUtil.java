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
