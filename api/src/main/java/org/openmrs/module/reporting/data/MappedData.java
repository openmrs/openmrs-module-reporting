/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * Extends the Mapped wrapper class for a DataDefintion, providing
 * a means for associating an optional DataConverter and format to the
 * particular instance of a Mapped Data Definition.
 * @see Mapped
 */
public class MappedData<T extends DataDefinition> extends Mapped<T> {

	public static final long serialVersionUID = 1L;
	
	//***********************
	// PROPERTIES
	//***********************
	
	private List<DataConverter> converters;
	
	//***********************
	// CONSTRUCTORS
	//***********************
	
	/**
	 * Default Constructor
	 */
	public MappedData() {
		super();
	}
	
	/**
	 * Constructor which allows you to set all available Map<String, Object>
	 */
	public MappedData(T dataDefinition, Map<String, Object> parameterMappings) {
		super(dataDefinition, parameterMappings);
	}
	
	/**
	 * Constructor which allows you to set all available properties
	 */
	public MappedData(T dataDefinition, Map<String, Object> parameterMappings, DataConverter... converters) {
		this(dataDefinition, parameterMappings);
		if (converters != null) {
			for (DataConverter c : converters) {
				addConverter(c);
			}
		}
	}
	
	//***********************
	// INSTANCE METHODS
	//***********************
		
	/** 
	 * @see Object#equals(Object)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			MappedData that = (MappedData)obj;
			if (ObjectUtil.areEqual(this.getConverters(), that.getConverters())) {
				return true;
			}
		}
		return false;
	}

	/** 
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = (converters == null ? hash : hash * converters.hashCode());
		return hash;
	}
	
	//***********************
	// PROPERTY ACCESS
	//***********************

	/**
	 * @return the converters
	 */
	public List<DataConverter> getConverters() {
		return converters;
	}

	/**
	 * @param converters the converters to set
	 */
	public void setConverters(List<DataConverter> converters) {
		this.converters = converters;
	}
	
	/**
	 * @param converter
	 */
	public void addConverter(DataConverter converter) {
		if  (converters == null) {
			converters = new ArrayList<DataConverter>();
		}
		converters.add(converter);
	}
}
