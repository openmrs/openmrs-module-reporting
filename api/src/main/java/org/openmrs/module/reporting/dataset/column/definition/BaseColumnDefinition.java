/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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