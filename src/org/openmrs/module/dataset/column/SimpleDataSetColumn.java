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
package org.openmrs.module.dataset.column;

import org.apache.commons.lang.StringUtils;

/**
 * Simple Implementation of a DataSetColumn
 */
public class SimpleDataSetColumn implements DataSetColumn {
	
	private String key;
	private String columnName;
	private String description;
	private Class<?> dataType;
	
	/**
	 * Default Constructor
	 */
	public SimpleDataSetColumn() { } 
	
	/**
	 * Constructor to populate all properties
	 */
	public SimpleDataSetColumn(String key, String columnName, String description, Class<?> dataType) {
		this.key = key;
		this.columnName = columnName;
		this.description = description;
		this.dataType = dataType;
	}

	/**
	 * Constructor to populate all properties, using the same description as key,
	 * and setting the Class to Object
	 */
	public SimpleDataSetColumn(String key) { 
		this(key, key, key, Object.class);
	}
	
	/**
	 * Constructor to populate all properties, using the same name and description as key
	 */
	public SimpleDataSetColumn(String key, Class<?> dataType) {
		this(key, key, key, dataType);
	}
	
	/**
	 * Constructor to populate all properties using the same name as key
	 */
	public SimpleDataSetColumn(String key, String description, Class<?> dataType) {
		this(key, key, description, dataType);
	}
	
    /**
     * @return the key
     */
    public String getKey() {
    	return key;
    }
	
    /**
     * @param key the key to set
     */
    public void setKey(String key) {
    	this.key = key;
    }

	/**
	 * @return the columnName
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * @param columnName the columnName to set
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
     * @return the description
     */
    public String getDescription() {
    	return description;
    }
	
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
    	this.description = description;
    }

    /**
     * @return the dataType
     */
    public Class<?> getDataType() {
    	return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(Class<?> dataType) {
    	this.dataType = dataType;
    }

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	return key;
    }

	/**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
   		if (obj instanceof DataSetColumn) {
   			DataSetColumn col = (DataSetColumn) obj;
			if (StringUtils.equals(this.getKey(), col.getKey())) {
				return true;
			}
		}
		return false;
    }

	/**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (this.getKey() == null ? 0 : this.getKey().hashCode());
		return hash;
    }

    /**
     * 
     */
	public int compareTo(DataSetColumn other) {		
		return this.getColumnName().compareTo(other.getColumnName());		
	}
}