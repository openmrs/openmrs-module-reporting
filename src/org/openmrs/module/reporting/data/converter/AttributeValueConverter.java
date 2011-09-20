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