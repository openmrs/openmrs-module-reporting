/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.person.definition;

import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Person Attribute Column
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.PersonAttributeDataDefinition")
public class PersonAttributeDataDefinition extends BaseDataDefinition implements PersonDataDefinition {
	
	public static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty
	private PersonAttributeType personAttributeType;
	
	//****** CONSTRUCTORS ******
	
	/**
	 * Default Constructor
	 */
	public PersonAttributeDataDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name and personAttributeType
	 */
	public PersonAttributeDataDefinition(String name, PersonAttributeType personAttributeType) {
		super(name);
		this.personAttributeType = personAttributeType;
	}
	
	/**
	 * Constructor to populate type only
	 */
	public PersonAttributeDataDefinition(PersonAttributeType personAttributeType) {
		this(null, personAttributeType);
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
	 * @return the personAttributeType
	 */
	public PersonAttributeType getPersonAttributeType() {
		return personAttributeType;
	}

	/**
	 * @param personAttributeType the personAttributeType to set
	 */
	public void setPersonAttributeType(PersonAttributeType personAttributeType) {
		this.personAttributeType = personAttributeType;
	}
}