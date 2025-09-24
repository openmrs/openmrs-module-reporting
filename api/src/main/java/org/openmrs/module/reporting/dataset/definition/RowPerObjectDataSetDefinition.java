/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset.definition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.common.SortCriteria.SortDirection;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * DataSetDefinition for Producing a DataSet that has one row per Object
 * @see DataSetDefinition
 */
public abstract class RowPerObjectDataSetDefinition extends BaseDataSetDefinition {
	
    public static final long serialVersionUID = 1L;
    
    //***** PROPERTIES *****
    
    @ConfigurationProperty
    private List<RowPerObjectColumnDefinition> columnDefinitions;
    
    @ConfigurationProperty
    private SortCriteria sortCriteria;
 
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
	public RowPerObjectDataSetDefinition(String name) { 
		super(name);
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @return the DataDefinition types that are supported by this DataSetDefinition instance
	 */
	public abstract List<Class<? extends DataDefinition>> getSupportedDataDefinitionTypes();
	
	/**
	 * @return all of the DataSetColumns that this DataSetDfinition will produce
	 */
	public List<DataSetColumn> getDataSetColumns() {
		List<DataSetColumn> l = new ArrayList<DataSetColumn>();
		for (ColumnDefinition cd : getColumnDefinitions()) {
			l.addAll(cd.getDataSetColumns());
		}
		return l;
	}
		
	/**
	 * Adds a new Column Definition given the passed parameters
	 */
	public abstract void addColumn(String name, DataDefinition dataDefinition, String mappings, DataConverter... converters);
	
	/**
	 * Adds a the Column Definitions defined in the passed DataSetDefinition
	 */
	public void addColumns(String name, RowPerObjectDataSetDefinition dataSetDefinition, String mappings, DataConverter... converters) {
		addColumns(name, dataSetDefinition, mappings, null, null, converters);
	}
	
	/**
	 * Adds a the Column Definitions defined in the passed DataSetDefinition
	 */
	public abstract void addColumns(String name, RowPerObjectDataSetDefinition dataSetDefinition, String mappings,
									TimeQualifier whichValues, Integer numberOfValues, DataConverter... converters);
	
	/**
	 * Returns the RowPerObjectColumnDefinition defined with the passed name
	 */
	public RowPerObjectColumnDefinition getColumnDefinition(String name) {
		for (RowPerObjectColumnDefinition cd : getColumnDefinitions()) {
			if (cd.getName().equals(name)) {
				return cd;
			}
		}
		return null;
	}
	
	/**
	 * Removes the RowPerObjectColumnDefinition defined with the passed name, returning true of the removal was successful
	 */
	public boolean removeColumnDefinition(String name) {
		for (Iterator<RowPerObjectColumnDefinition> i = getColumnDefinitions().iterator(); i.hasNext();) {
			RowPerObjectColumnDefinition cd = i.next();
			if (ObjectUtil.areEqual(name, cd.getName())) {
				i.remove();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Add sort criteria to this data set
	 */
	public void addSortCriteria(String columnName, SortDirection direction) {
		if (sortCriteria == null) {
			sortCriteria = new SortCriteria();
		}
		sortCriteria.addSortElement(columnName, direction);
	}
	
    //***** PROPERTY ACCESS *****

	/**
	 * @return the columnDefinitions
	 */
	public List<RowPerObjectColumnDefinition> getColumnDefinitions() {
		if (columnDefinitions == null) {
			columnDefinitions = new ArrayList<RowPerObjectColumnDefinition>();
		}
		return columnDefinitions;
	}

	/**
	 * @param columnDefinitions the columnDefinitions to set
	 */
	public void setColumnDefinitions(List<RowPerObjectColumnDefinition> columnDefinitions) {
		this.columnDefinitions = columnDefinitions;
	}

	/**
	 * @return the sortCriteria
	 */
	public SortCriteria getSortCriteria() {
		return sortCriteria;
	}

	/**
	 * @param sortCriteria the sortCriteria to set
	 */
	public void setSortCriteria(SortCriteria sortCriteria) {
		this.sortCriteria = sortCriteria;
	}
}