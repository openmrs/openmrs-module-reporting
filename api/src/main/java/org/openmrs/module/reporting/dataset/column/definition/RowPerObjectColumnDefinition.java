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
import java.util.Map;

import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;

/**
 * Row Per Object column definition
 */
public class RowPerObjectColumnDefinition extends BaseColumnDefinition {
	
	public static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
    @ConfigurationProperty
    private MappedData<? extends DataDefinition> dataDefinition;
    
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public RowPerObjectColumnDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name only
	 */
	public RowPerObjectColumnDefinition(String name) {
		super(name);
	}
	
	/**
	 * Constructor to populate name and data definition
	 */
	public RowPerObjectColumnDefinition(String name, MappedData<? extends DataDefinition> dataDefinition) {
		this(name);
		this.dataDefinition = dataDefinition;
	}
	
	/**
	 * Constructor to populate name, data definition, and converters
	 */
	public RowPerObjectColumnDefinition(String name, DataDefinition dataDefinition, String mappings, DataConverter... converters) {
		this(name, new MappedData<DataDefinition>(dataDefinition, ParameterizableUtil.createParameterMappings(mappings), converters));
	}
	
	/**
	 * Constructor to populate name, data definition, and converters
	 */
	public RowPerObjectColumnDefinition(String name, DataDefinition dataDefinition, Map<String, Object> mappings, DataConverter... converters) {
		this(name, new MappedData<DataDefinition>(dataDefinition, mappings, converters));
	}
	
	/**
	 * @see ColumnDefinition#getDataSetColumns()
	 */
	public List<DataSetColumn> getDataSetColumns() {
		List<DataSetColumn> l = new ArrayList<DataSetColumn>();
		if (dataDefinition != null && dataDefinition.getParameterizable() != null) {
			Class<?> type = dataDefinition.getParameterizable().getDataType();
			List<DataConverter> converters = dataDefinition.getConverters();
			if (converters != null && converters.size() > 0) {
				type = converters.get(converters.size() - 1).getDataType();
			}
			l.add(new DataSetColumn(getName(), getName(), type));
		}
		return l;
	}

    //***** Property Access *****

	/**
	 * @return the dataDefinition
	 */
	public MappedData<? extends DataDefinition> getDataDefinition() {
		return dataDefinition;
	}
	
	/**
	 * @param dataDefinition the dataDefinition to set
	 */
	public void setDataDefinition(MappedData<? extends DataDefinition> dataDefinition) {
		this.dataDefinition = dataDefinition;
	}
}