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

import java.util.List;

import org.openmrs.PersonAttributeType;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Cohort query that returns patients with certain person attribute types and values.
 */
@Localized("reporting.PersonAttributeCohortDefinition")
public class PersonAttributeCohortDefinition extends BaseCohortDefinition {
	
    private static final long serialVersionUID = 1L;
    
	//***** PROPERTIES *****
    /**
     * Configuration group that allows you to match 
     * on the person attribute type.
     */
    @ConfigurationProperty(group="attributeTypeGroup")
	private PersonAttributeType attributeType;

    /**
     * Configuration group that allows you to match 
     * on the person attribute value.
     */    
    @ConfigurationProperty(group="attributeValuesGroup")
	private List<String> values;
      
    
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
		/*
		sb.append("Patients with ");
		sb.append(attribute != null ? attribute.getName() : " any attribute");
		if (values != null) {
			sb.append(" in the set of (");
			sb.append(values);
			sb.append(")");
		}
		*/
		return sb.toString();
	}

	/**
	 * @return the attributeType
	 */
	public PersonAttributeType getAttributeType() {
		return attributeType;
	}

	/**
	 * @param attributeType the attributeType to set
	 */
	public void setAttributeType(PersonAttributeType attributeType) {
		this.attributeType = attributeType;
	}

	/**
	 * @return the values
	 */
	public List<String> getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(List<String> values) {
		this.values = values;
	}
	
	
}
