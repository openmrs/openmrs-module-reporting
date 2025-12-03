/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
	 * @param data the data to set
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
