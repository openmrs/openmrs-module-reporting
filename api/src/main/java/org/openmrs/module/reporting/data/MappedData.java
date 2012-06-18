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
