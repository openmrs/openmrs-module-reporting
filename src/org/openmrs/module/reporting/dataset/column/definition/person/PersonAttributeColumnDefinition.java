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
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.reporting.dataset.column.converter.AttributeValueConverter;
import org.openmrs.module.reporting.dataset.column.converter.ColumnConverter;
import org.openmrs.module.reporting.dataset.column.definition.LinkedPropertyColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.PropertyColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;

/**
 * Person Attribute Column
 */
public class PersonAttributeColumnDefinition extends LinkedPropertyColumnDefinition<PersonAttributeType> implements PersonColumnDefinition {
	
	public static final long serialVersionUID = 1L;
	
	//****** CONSTRUCTORS ******
	
	/**
	 * Default Constructor
	 */
	public PersonAttributeColumnDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name only
	 */
	public PersonAttributeColumnDefinition(PersonAttributeType personAttributeType, String name) {
		super(personAttributeType, name);
	}
	
	/**
	 * Constructor to populate all properties
	 */
	public PersonAttributeColumnDefinition(PersonAttributeType personAttributeType, String name, ColumnConverter transform) {
		super(personAttributeType, name, transform);
	}
	
	//****** INSTANCE METHODS ******

	/** 
	 * @see RowPerObjectColumnDefinition#getBaseType()
	 */
	public Class<? extends OpenmrsData> getBaseType() {
		return PersonAttribute.class;
	}
	
	/** 
	 * @see RowPerObjectColumnDefinition#getIdProperty()
	 */
	public String getIdProperty() {
		return "personId";
	}

	/** 
	 * @see PropertyColumnDefinition#getProperty()
	 */
	@Override
	public String getProperty() {
		return "value";
	}

	/** 
	 * @see PropertyColumnDefinition#getPropertyTransform()
	 */
	@Override
	public ColumnConverter getPropertyConverter() {
		return new AttributeValueConverter(getMetadata());
	}
}