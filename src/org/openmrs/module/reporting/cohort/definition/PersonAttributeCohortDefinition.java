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
package org.openmrs.module.reporting.cohort.definition;

import org.openmrs.PersonAttributeType;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class PersonAttributeCohortDefinition extends BaseCohortDefinition {
	
    private static final long serialVersionUID = 1L;
    
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
	private PersonAttributeType attribute;
	
	@ConfigurationProperty(required=true)
	private String value;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * This currently only returns patients, although it's named for persons.
	 */
	public PersonAttributeCohortDefinition() {
		super();
	}

	//***** INSTANCE METHODS *****
	
    /**
     * @see java.lang.Object#toString()
     */
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("Patients with ");
		sb.append(attribute != null ? attribute.getName() : " any attribute");
		if (value != null) {
			sb.append(" equal to ");
			sb.append(value);
		}
		return sb.toString();
	}

    /**
     * @return the attribute
     */
    public PersonAttributeType getAttribute() {
    	return attribute;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(PersonAttributeType attribute) {
    	this.attribute = attribute;
    }

    /**
     * @return the value
     */
    public String getValue() {
    	return value;
    }
	
    /**
     * @param value the value to set
     */
    public void setValue(String value) {
    	this.value = value;
    }
}
