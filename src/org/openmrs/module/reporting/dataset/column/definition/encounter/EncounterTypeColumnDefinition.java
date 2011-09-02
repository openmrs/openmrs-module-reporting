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
package org.openmrs.module.reporting.dataset.column.definition.encounter;

import org.openmrs.module.reporting.dataset.column.definition.person.PersonPropertyColumnDefinition;
import org.openmrs.module.reporting.dataset.column.converter.ColumnConverter;

/**
 * Encounter Type Column
 */
public class EncounterTypeColumnDefinition extends EncounterPropertyColumnDefinition {
	
	public static final long serialVersionUID = 1L;
	
	/**
	 * Default Constructor
	 */
	public EncounterTypeColumnDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name only
	 */
	public EncounterTypeColumnDefinition(String name) {
		super(name);
	}
	
	/**
	 * Constructor to populate name and transform
	 */
	public EncounterTypeColumnDefinition(String name, ColumnConverter transform) {
		super(name, transform);
	}

	/** 
	 * @see PersonPropertyColumnDefinition#getProperty()
	 */
	@Override
	public String getProperty() {
		return "encounterType";
	}
}