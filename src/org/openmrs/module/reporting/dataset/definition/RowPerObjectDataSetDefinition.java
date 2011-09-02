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
package org.openmrs.module.reporting.dataset.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;

/**
 * DataSetDefinition for Producing a DataSet that has one row per OpenmrsData type
 * @see DataSetDefinition
 */
public class RowPerObjectDataSetDefinition<T extends ColumnDefinition> extends BaseDataSetDefinition {
	
    public static final long serialVersionUID = 1L;
    
    //***** PROPERTIES *****
    
    @ConfigurationProperty
    private List<Mapped<T>> columnDefinitions;
 
    //***** CONSTRUCTORS *****
    
    /**
     * Default Constructor
     */
    public RowPerObjectDataSetDefinition() {
    	super();
    }

	/**
	 * Public constructor
	 */
	public RowPerObjectDataSetDefinition(String name, String description) { 
		this();
		this.setName(name);
		this.setDescription(description);
	}
	
	//***** INSTANCE METHODS *****

	/**
	 * @param column the ColumnDefinition to add
	 */
	public void addColumnDefinition(Mapped<T> columnDefinition) {
		getColumnDefinitions().add(columnDefinition);
	}

	/**
	 * @param columnDefinition the ColumnDefinition to add
	 */
	public void addColumnDefinition(T columnDefinition) {
		addColumnDefinition(columnDefinition, "");
	}
	
	/**
	 * @param columnDefinition the ColumnDefinition to add
	 */
	public void addColumnDefinition(T columnDefinition, Map<String, Object> mappings) {
		addColumnDefinition(new Mapped<T>(columnDefinition, mappings));
	}
	
	/**
	 * @param columnDefinition the ColumnDefinition to add
	 */
	public void addColumnDefinition(T columnDefinition, String mappings) {
		addColumnDefinition(new Mapped<T>(columnDefinition, ParameterizableUtil.createParameterMappings(mappings)));
	}
	
    //***** PROPERTY ACCESS *****

	/**
	 * @return the columnDefinitions
	 */
	public List<Mapped<T>> getColumnDefinitions() {
		if (columnDefinitions == null) {
			columnDefinitions = new ArrayList<Mapped<T>>();
		}
		return columnDefinitions;
	}

	/**
	 * @param columnDefinitions the columnDefinitions to set
	 */
	public void setColumnDefinitions(List<Mapped<T>> columnDefinitions) {
		this.columnDefinitions = columnDefinitions;
	}
}