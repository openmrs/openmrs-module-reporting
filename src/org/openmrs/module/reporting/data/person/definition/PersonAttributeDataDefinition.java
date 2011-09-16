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
package org.openmrs.module.reporting.data.person.definition;

import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;

/**
 * Person Attribute Column
 */
public class PersonAttributeDataDefinition extends BaseDataDefinition implements PersonDataDefinition {
	
	public static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	private PersonAttributeType type;
	
	//****** CONSTRUCTORS ******
	
	/**
	 * Default Constructor
	 */
	public PersonAttributeDataDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name and type
	 */
	public PersonAttributeDataDefinition(String name, PersonAttributeType type) {
		super(name);
		this.type = type;
	}
	
	/**
	 * Constructor to populate type only
	 */
	public PersonAttributeDataDefinition(PersonAttributeType type) {
		this(null, type);
	}

	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return PersonAttribute.class;
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the type
	 */
	public PersonAttributeType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(PersonAttributeType type) {
		this.type = type;
	}
}