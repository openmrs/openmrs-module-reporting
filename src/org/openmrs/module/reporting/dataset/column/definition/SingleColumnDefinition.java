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

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.column.converter.ColumnConverter;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;

/**
 * Base column definition
 */
public class SingleColumnDefinition extends BaseColumnDefinition {
	
	public static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
    @ConfigurationProperty
    private Mapped<? extends DataDefinition> dataDefinition;
    
	@ConfigurationProperty
	private ColumnConverter converter;
    
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public SingleColumnDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name only
	 */
	public SingleColumnDefinition(String name) {
		super(name);
	}
	
	/**
	 * Constructor to populate to populate name and data definition
	 */
	public SingleColumnDefinition(String name, Mapped<? extends DataDefinition> dataDefinition) {
		this(name, dataDefinition, null);
	}
	
	/**
	 * Constructor to populate all properties
	 */
	public SingleColumnDefinition(String name, Mapped<? extends DataDefinition> dataDefinition, ColumnConverter converter) {
		super(name);
		this.dataDefinition = dataDefinition;
		this.converter = converter;
	}
	
	/**
	 * Constructor to populate name and data definition
	 */
	public SingleColumnDefinition(String name, DataDefinition dataDefinition, String mappings, ColumnConverter converter) {
		this(name, new Mapped<DataDefinition>(dataDefinition, ParameterizableUtil.createParameterMappings(mappings)), converter);
	}
	
	/**
	 * @see ColumnDefinition#getDataSetColumns()
	 */
	public List<DataSetColumn> getDataSetColumns() {
		List<DataSetColumn> l = new ArrayList<DataSetColumn>();
		if (dataDefinition != null && dataDefinition.getParameterizable() != null) {
			Class<?> type = dataDefinition.getParameterizable().getDataType();
			if (converter != null) {
				type = converter.getDataType();
			}
			l.add(new DataSetColumn(getName(), getName(), type));
		}
		return l;
	}

    //***** Property Access *****

	/**
	 * @return the dataDefinition
	 */
	public Mapped<? extends DataDefinition> getDataDefinition() {
		return dataDefinition;
	}
	
	/**
	 * @param dataDefinition the dataDefinition to set
	 */
	public void setDataDefinition(Mapped<? extends DataDefinition> dataDefinition) {
		this.dataDefinition = dataDefinition;
	}

	/**
	 * @return the converter
	 */
	public ColumnConverter getConverter() {
		return converter;
	}

	/**
	 * @param converter the converter to set
	 */
	public void setConverter(ColumnConverter converter) {
		this.converter = converter;
	}
}