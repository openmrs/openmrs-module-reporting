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
package org.openmrs.module.dataset.definition;

import java.util.List;

import org.openmrs.module.evaluation.parameter.BaseParameterizable;

/**
 * Base Implementation of a DataSetDefinition which provides core method
 * implementations for handling Parameters and common Property values
 * @see DataSetDefinition
 */
public abstract class BaseDataSetDefinition extends BaseParameterizable implements DataSetDefinition {
	
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
	
	
	/**
	 * 
	 */
	public List<Class> getColumnDatatypes() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 */
	public List<String> getColumnKeys() {
		// TODO Auto-generated method stub
		return null;
	}	
	
	/**
	 * 
	 */
	public Integer getColumnCount() { 
		return this.getColumns().size();
	}
	
}