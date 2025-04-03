/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.converter;

import org.openmrs.Attributable;
import org.openmrs.PersonAttributeType;
import org.openmrs.util.OpenmrsClassLoader;

/**
 * Date data converter
 */
public class AttributeValueConverter implements DataConverter {
	
	//***** PROPERTIES *****
	
	private PersonAttributeType personAttributeType;
	
	//***** CONSTRUCTORS *****
	
	public AttributeValueConverter() {}
	
	/**
	 * Full Constructor
	 */
	public AttributeValueConverter(PersonAttributeType personAttributeType) {
		this.personAttributeType = personAttributeType;
	}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#converter(Object)
	 * @should convert a serialized attribute value into its hydrated object form
	 * @should return the passed in value if it is not attributable
	 */
	public Object convert(Object original) {
		String value = (String) original;
		if (value != null) {
			try {
				Object o = getDataType().newInstance();
				if (o instanceof Attributable) {
					Attributable<?> attr = (Attributable<?>) o;
					return attr.hydrate(value);
				}
			}
			catch (Exception e) {
				throw new RuntimeException("Unable to hydrate " + original + " for format " + personAttributeType.getFormat());
			}
		}
		return value;
	}
	
	/** 
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		try {
			return OpenmrsClassLoader.getInstance().loadClass(personAttributeType.getFormat());
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to load class.", e);
		}
	}
	
	/** 
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return String.class;
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