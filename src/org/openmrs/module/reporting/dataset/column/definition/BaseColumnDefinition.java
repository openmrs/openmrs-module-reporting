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

import org.openmrs.module.reporting.evaluation.BaseDefinition;

/**
 * Base Column Definition
 */
public abstract class BaseColumnDefinition extends BaseDefinition implements ColumnDefinition {
	
	public static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	private Integer id;
    
	/**
	 * Default Constructor
	 */
	public BaseColumnDefinition() {
		super();
	}

	/**
	 * Constructor to populate all properties
	 */
	public BaseColumnDefinition(String name) {
		setName(name);
	}
	
	//****** INSTANCE METHODS ******
    
	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	return getName();
    }
	
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
}