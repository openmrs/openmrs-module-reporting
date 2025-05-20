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