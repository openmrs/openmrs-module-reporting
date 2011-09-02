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

import org.openmrs.OpenmrsMetadata;
import org.openmrs.module.reporting.dataset.column.converter.ColumnConverter;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Linked MetaData Column Definition
 */
public abstract class LinkedPropertyColumnDefinition<T extends OpenmrsMetadata> extends PropertyColumnDefinition {
	
	public static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
	private T metadata;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public LinkedPropertyColumnDefinition() {
		super();
	}

	/**
	 * Constructor to populate name only
	 */
	public LinkedPropertyColumnDefinition(T metadata, String name) {
		super(name);
		this.metadata = metadata;
	}
	
	/**
	 * Constructor to populate all properties
	 */
	public LinkedPropertyColumnDefinition(T metadata, String name, ColumnConverter transform) {
		super(name, transform);
		this.metadata = metadata;
	}

	//***** PROPERTY ACCESS *****

	/**
	 * @return the metadata
	 */
	public T getMetadata() {
		return metadata;
	}

	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(T metadata) {
		this.metadata = metadata;
	}
}