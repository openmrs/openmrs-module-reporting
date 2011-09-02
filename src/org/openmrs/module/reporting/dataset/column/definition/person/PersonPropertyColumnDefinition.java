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
package org.openmrs.module.reporting.dataset.column.definition.person;

import org.openmrs.OpenmrsData;
import org.openmrs.Person;
import org.openmrs.module.reporting.dataset.column.definition.PropertyColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;
import org.openmrs.module.reporting.dataset.column.converter.ColumnConverter;

/**
 * Person Property Column Definition
 */
public abstract class PersonPropertyColumnDefinition extends PropertyColumnDefinition implements PersonColumnDefinition {
	
	public static final long serialVersionUID = 1L;
	
	/**
	 * Default Constructor
	 */
	public PersonPropertyColumnDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name only
	 */
	public PersonPropertyColumnDefinition(String name) {
		super(name);
	}
	
	/**
	 * Constructor to populate name and transform
	 */	
	public PersonPropertyColumnDefinition(String name, ColumnConverter transform) {
		super(name, transform);
	}

	/** 
	 * @see PropertyColumnDefinition#getBaseType()
	 */
	public Class<? extends OpenmrsData> getBaseType() {
		return Person.class;
	}
	
	/** 
	 * @see RowPerObjectColumnDefinition#getIdProperty()
	 */
	public String getIdProperty() {
		return "personId";
	}
}