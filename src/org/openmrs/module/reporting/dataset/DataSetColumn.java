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
package org.openmrs.module.reporting.dataset;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * A DataSetColumn is a generic way to store the information that
 * makes up a column in a DataSet.
 */
public class DataSetColumn implements Comparable<DataSetColumn>, Serializable {
	
	private static final long serialVersionUID = 1L;

	//***** PROPERTIES *****
	
	private String columnKey;
	private String displayName;
	private String description;
	private Class<?> dataType;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public DataSetColumn() { } 
	
	/**
	 * Constructor to populate all properties
	 */
	public DataSetColumn(String columnKey, String displayName, String description, Class<?> dataType) {
		this();
		this.columnKey = columnKey;
		this.displayName = displayName;
		this.description = description;
		this.dataType = dataType;
	}
	
	/**
	 * Constructor to populate key, name, type
	 */
	public DataSetColumn(String columnKey, String displayName, Class<?> dataType) {
		this(columnKey, displayName, null, dataType);
	}
	
	//***** INSTANCE METHODS *****

	/**
     * @see Object#toString()
     */
    @Override
    public String toString() {
    	return getColumnKey() + " " + getDisplayName();
    }

	/**
     * @see Object#equals(Object)
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
     * @see Object#hashCode()
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