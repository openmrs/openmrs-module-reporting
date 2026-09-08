/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset.definition;

import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.BaseDefinition;

/**
 * Base Implementation of a DataSetDefinition which provides core method
 * implementations for handling Parameters and common Property values
 * @see DataSetDefinition
 */
public abstract class BaseDataSetDefinition extends BaseDefinition implements DataSetDefinition {
	
    public static final long serialVersionUID = 1L;
    
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
	 * @param name
	 */
	public BaseDataSetDefinition(String name) { 
		this();
		this.setName(name);
	}

	/**
	 * Public constructor
	 * @param name
	 * @param description
	 */
	public BaseDataSetDefinition(String name, String description) { 
		this(name);
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
		return (getUuid() == null ? super.hashCode() : 31 * getUuid().hashCode());
	}
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return ObjectUtil.nvlStr(getName(), super.toString());
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