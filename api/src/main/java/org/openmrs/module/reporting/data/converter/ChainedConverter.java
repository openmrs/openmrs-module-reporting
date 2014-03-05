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

import java.util.ArrayList;
import java.util.List;

/**
 * Combines multiple converters together
 */
public class ChainedConverter implements DataConverter {
		
	//***** PROPERTIES *****
	
	private List<DataConverter> converters;
	
	//***** CONSTRUCTORS *****
	
	public ChainedConverter() {}
	
	/**
	 * Full Constructor
	 */
	public ChainedConverter(DataConverter... converters) {
		if (converters != null) {
			for (DataConverter c : converters) {
				addConverter(c);
			}
		}
	}
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see DataConverter#convert(Object)
	 */
	public Object convert(Object original) {
		Object o = original;
		if (converters != null) {
			for (DataConverter converter : getConverters()) {
				o = converter.convert(o);
			}
		}
		return o;
	}
	
	/** 
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		if (converters.size() > 0) {
			return converters.get(converters.size()-1).getDataType();
		}
		return Object.class;
	}
	
	/** 
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		if (converters.size() > 0) {
			return converters.get(0).getInputDataType();
		}
		return Object.class;
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the converters
	 */
	public List<DataConverter> getConverters() {
		return converters;
	}
	
	/**
	 * @param converter the converter to add
	 */
	public void addConverter(DataConverter converter) {
		if (converters == null) {
			converters = new ArrayList<DataConverter>();
		}
		converters.add(converter);
	}

	/**
	 * @param converters the converters to set
	 */
	public void setConverters(List<DataConverter> converters) {
		this.converters = converters;
	}
}