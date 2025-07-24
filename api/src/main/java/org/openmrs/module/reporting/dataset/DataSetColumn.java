/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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