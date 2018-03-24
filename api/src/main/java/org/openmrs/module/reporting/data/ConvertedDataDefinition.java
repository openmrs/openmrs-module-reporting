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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

/**
 * Abstract Adapter class for easily combining an existing data definition and a List of Data Converters
 * to return a transformation of the underlying data definition or to alter the parameters that are
 * exposed by the the underlying data definition
 */
public abstract class ConvertedDataDefinition<T extends DataDefinition> extends BaseDataDefinition {

	public static final long serialVersionUID = 1L;
	
	//***********************
	// PROPERTIES
	//***********************

	@ConfigurationProperty(required=true)
	private Mapped<T> definitionToConvert;

	@ConfigurationProperty
	private List<DataConverter> converters;
	
	//***********************
	// CONSTRUCTORS
	//***********************
	
	/**
	 * Default Constructor
	 */
	public ConvertedDataDefinition() {
		super();
	}

	/**
	 * Constructor including data definition which automatically maps parameters
	 */
	public ConvertedDataDefinition(String name, T definitionToConvert, DataConverter...converters) {
		super(name);
		Map<String, Object> mappings = new HashMap<String, Object>();
		for (Parameter p : definitionToConvert.getParameters()) {
			addParameter(p);
			mappings.put(p.getName(), "${"+p.getName()+"}");
		}
		this.definitionToConvert = new Mapped<T>(definitionToConvert, mappings);
		addConverter(converters);
	}

	/**
	 * Constructor including data definition which accepts explicit parameter mappings
	 */
	public ConvertedDataDefinition(String name, T definitionToConvert, Map<String, Object> mappings, DataConverter...converters) {
		super(name);
		this.definitionToConvert = new Mapped<T>(definitionToConvert, mappings);
		addConverter(converters);
	}
	
	//***********************
	// INSTANCE METHODS
	//***********************

	@Override
	public Class<?> getDataType() {
		if (getConverters().isEmpty()) {
			return getDefinitionToConvert().getParameterizable().getDataType();
		}
		return getConverters().get(getConverters().size()-1).getDataType();
	}


	//***********************
	// PROPERTY ACCESS
	//***********************

	public Mapped<T> getDefinitionToConvert() {
		return definitionToConvert;
	}

	public void setDefinitionToConvert(Mapped<T> definitionToConvert) {
		this.definitionToConvert = definitionToConvert;
	}

	public List<DataConverter> getConverters() {
		if (converters == null) {
			converters = new ArrayList<DataConverter>();
		}
		return converters;
	}

	public void setConverters(List<DataConverter> converters) {
		this.converters = converters;
	}

	public void addConverter(DataConverter... converters) {
		if (converters != null) {
			for (DataConverter c : converters) {
				getConverters().add(c);
			}
		}
	}
}
