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
import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * A DataSetColumn is a generic way to store the information that
 * makes up a column in a DataSet.
 */
public class DataSetColumn implements Comparable<DataSetColumn>, Serializable {
	
	public static final long serialVersionUID = 1L;

	//***** PROPERTIES *****
	
	private String name;
	private String label;
	private Class<?> dataType;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public DataSetColumn() { } 
	
	/**
	 * Constructor to populate all properties
	 */
	public DataSetColumn(String name, String label, Class<?> dataType) {
		this();
		this.name = name;
		this.label = label;
		this.dataType = dataType;
	}
	
	//***** INSTANCE METHODS *****

	/**
     * @see Object#toString()
     */
    @Override
    public String toString() {
    	return ObjectUtil.nvl(getLabel(), getName());
    }

	/**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
   		if (obj instanceof DataSetColumn) {
   			DataSetColumn col = (DataSetColumn) obj;
			if (StringUtils.equals(this.getName(), col.getName())) {
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
		hash = 31 * hash + (this.getName() == null ? 0 : this.getName().hashCode());
		return hash;
    }

    /**
     * @see Comparable#compareTo(Object)
     */
	public int compareTo(DataSetColumn other) {		
		return this.toString().compareTo(other.toString());		
	}
	
	//****** PROPERTY ACCESS ******

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
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