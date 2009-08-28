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
	
	private static final long serialVersionUID = 1L;

	/**
	 * Properties
	 */
	private String columnKey;
	private String displayName;
	private String description;
	private Class<?> dataType;
	
	/**
	 * Default Constructor
	 */
	public SimpleDataSetColumn() { } 
	
	/**
	 * Constructor to populate all properties
	 */
	public SimpleDataSetColumn(String columnKey, String displayName, String description, Class<?> dataType) {
		this.columnKey = columnKey;
		this.displayName = displayName;
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	return getColumnKey() + " " + getDisplayName();
    }

	/**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
   		if (obj instanceof DataSetColumn) {
   			DataSetColumn col = (DataSetColumn) obj;
			if (StringUtils.equals(this.getColumnKey(), col.getColumnKey())) {
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
		hash = 31 * hash + (this.getColumnKey() == null ? 0 : this.getColumnKey().hashCode());
		return hash;
    }

    /**
     * @see Comparable#compareTo(Object)
     */
	public int compareTo(DataSetColumn other) {		
		return this.getDisplayName().compareTo(other.getDisplayName());		
	}
	
	//****** PROPERTY ACCESS ******

	/**
	 * @return the columnKey
	 */
	public String getColumnKey() {
		return columnKey;
	}

	/**
	 * @param columnKey the columnKey to set
	 */
	public void setColumnKey(String columnKey) {
		this.columnKey = columnKey;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
}