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
package org.openmrs.module.reporting.dataset.column.definition;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.reporting.dataset.column.converter.ColumnConverter;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.BaseDefinition;

/**
 * Base Column Definition
 */
public abstract class BaseColumnDefinition extends BaseDefinition implements ColumnDefinition {
	
	public static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	private Integer id;
	
	@ConfigurationProperty
	private ColumnConverter converter;
    
	/**
	 * Default Constructor
	 */
	public BaseColumnDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name only
	 */
	public BaseColumnDefinition(String name) {
		this(name, null, null);
	}
	
	/**
	 * Constructor to populate name and description only
	 */
	public BaseColumnDefinition(String name, String description) {
		this(name, description, null);
	}

	/**
	 * Constructor to populate all properties
	 */
	public BaseColumnDefinition(String name, String description, ColumnConverter converter) {
		setName(name);
		setDescription(description);
		setConverter(converter);
	}
	
	//****** INSTANCE METHODS ******
    
	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	return getName();
    }

	/**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
   		if (obj instanceof ColumnDefinition) {
   			ColumnDefinition col = (ColumnDefinition)obj;
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
	 * @see ColumnDefinition#getDataType()
	 */
	public final Class<?> getDataType() {
		if (getConverter() != null) {
			return getConverter().getDataType();
		}
		return getRawDataType();
	}
	
	/**
	 * @return the rawDataType
	 */
	public abstract Class<?> getRawDataType();
	
    //***** Property Access *****

	/**
     * @return the id
     */
    public Integer getId() {
    	return id;
    }

	/**
     * @param id the id to set
     */
    public void setId(Integer id) {
    	this.id = id;
    }
    
	/** 
	 * @see ColumnDefinition#getConverter()
	 */
	public ColumnConverter getConverter() {
		return converter;
	}

	/**
	 * @param converter the converter to set
	 */
	public void setConverter(ColumnConverter converter) {
		this.converter = converter;
	}
}