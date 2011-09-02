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

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.dataset.column.converter.ColumnConverter;

/**
 * Person Column
 */
public abstract class PropertyColumnDefinition extends BaseColumnDefinition implements RowPerObjectColumnDefinition {
	
	public static final long serialVersionUID = 1L;

	/**
	 * Constructor to populate name only
	 */
	public PropertyColumnDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name only
	 */
	public PropertyColumnDefinition(String name) {
		super(name);
	}
	
	/**
	 * Constructor to populate all properties
	 */
	public PropertyColumnDefinition(String name, ColumnConverter converter) {
		super(name, null, converter);
	}

	/**
	 * @return the property that this Column represents
	 */
	public abstract String getProperty();
	
	/**
	 * Allows for ColumnDefinitions to return a different type than the original property, if needed.
	 * This is separate from the converter property, in that this is meant to be defined in the ColumnDefinition
	 * implementation if the aim is to always return a type that is different than the property type.
	 * The converter property can then still be used by the end user to further convert the value
	 * An example would be if you wanted to create an AgeColumnDefinition as a PropertyColumnDefinition.  
	 * This ColumnDefinition would refer to the birthdate property on the Person object, and this method
	 * would be responsible for transforming that to an Age datatype.
	 * 
	 * @return provides a means for transforming a Property before returning it
	 */
	public ColumnConverter getPropertyConverter() {
		return null;
	}

	/** 
	 * @see BaseColumnDefinition#getRawDataType()
	 */
	@Override
	public Class<?> getRawDataType() {
		if (getPropertyConverter() != null) {
			return getPropertyConverter().getDataType();
		}
		if (StringUtils.isEmpty(getProperty())) {
			return getBaseType();
		}
		Field f = ReflectionUtil.getField(getBaseType(), getProperty());
		return ReflectionUtil.getFieldType(f);
	}
}