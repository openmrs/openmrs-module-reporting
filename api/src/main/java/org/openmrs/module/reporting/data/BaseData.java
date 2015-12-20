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

import java.util.HashMap;
import java.util.Map;

/**
 * Provides abstract implementation of the Data interface
 */
public abstract class BaseData implements Data {

	//***** PROPERTIES *****

    private Map<Integer, Object> data;

    //***** CONSTRUCTORS *****

    public BaseData() {
    	super();
    }

    //***** PROPERTY ACCESS *****

	/**
	 * @return the data
	 */
	public Map<Integer, Object> getData() {
		if (data == null) {
			data = new HashMap<Integer, Object>();
		}
		return data;
	}

	/**
	 * @param data: the data to set
	 */
	public void setData(Map<Integer, Object> data) {
		this.data = data;
	}

	/**
	 * Adds a data item with the passed id and value
	 */
	public void addData(Integer id, Object value) {
		getData().put(id, value);
	}

	/**
	 * Sets/replaces all values with the given values
	 */
	public void replaceData(Map<Integer, ? extends Object> data) {
		this.data = null;
		if (data != null) {
			for (Integer id : data.keySet()) {
				addData(id, (Object)data.get(id));
			}
		}
	}
}
