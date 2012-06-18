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
package org.openmrs.module.reporting.data;

import java.util.List;

import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Data utility classes
 */
public class DataUtil {

	/**
	 * @return the data passed through zero or more data converters
	 */
	public static Object convertData(Object data, DataConverter...converters) {
		Object ret = data;
		if (converters != null) {
			for (DataConverter c : converters) {
				ret = c.convert(ret);
			}
		}
		return ret;
	}
	
	/**
	 * @return the data passed through zero or more data converters
	 */
	public static Object convertData(Object data, List<DataConverter> converters) {
		Object ret = data;
		if (converters != null) {
			for (DataConverter c : converters) {
				ret = c.convert(ret);
			}
		}
		return ret;
	}
}
