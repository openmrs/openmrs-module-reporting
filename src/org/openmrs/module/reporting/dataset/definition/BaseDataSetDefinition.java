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
package org.openmrs.module.reporting.dataset.definition;

import org.openmrs.module.reporting.evaluation.BaseDefinition;

/**
 * Base Implementation of a DataSetDefinition which provides core method
 * implementations for handling Parameters and common Property values
 * @see DataSetDefinition
 */
public abstract class BaseDataSetDefinition extends BaseDefinition implements DataSetDefinition {
	
    private static final long serialVersionUID = 1L;
    
    //***** PROPERTIES *****
    private Integer id;
 
    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public BaseDataSetDefinition() {
    	super();
    }

	/**
	 * Public constructor
	 * 
	 * @param name
	 * @param description
	 * @param questions
	 */
	public BaseDataSetDefinition(String name, String description) { 
		this();
		this.setName(name);
		this.setDescription(description);
	}

	//***** INSTANCE METHODS *****

	/** @see Object#equals(Object) */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof DataSetDefinition) {
			DataSetDefinition p = (DataSetDefinition) obj;
			if (this.getUuid() != null) {
				return (this.getUuid().equals(p.getUuid()));
			}
		}
		return this == obj;
	}
	
	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (getUuid() == null ? 0 : 31 * getUuid().hashCode());
	}
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

	
    //***** PROPERTY ACCESS *****
    
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
}