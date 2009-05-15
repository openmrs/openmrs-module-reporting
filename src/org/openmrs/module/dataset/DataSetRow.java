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
package org.openmrs.module.dataset;

import java.util.HashMap;
import java.util.Map;


/**
 * A dataset row is a generic way to store a map of key-value pairs that make up 
 * a row in a dataset.
 */
public class DataSetRow {
	
	public Map<String, Object> row = new HashMap<String, Object>();
	
	public DataSetRow() { } 
	
	public void putValue(String key, Object value) { 
		row.put(key, value);
	}
	
	public Object getValue(String key) { 
		return row.get(key);
	}
	
	
	
}
