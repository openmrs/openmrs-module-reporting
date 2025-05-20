/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset;

import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A basic implementation of a DataSet.
 */
public class SimpleDataSet implements DataSet {
    
    private DataSetDefinition definition;
    private EvaluationContext context;
    private SimpleDataSetMetaData metaData = new SimpleDataSetMetaData();
    private Map<Integer, DataSetRow> idToRowMap = new LinkedHashMap<Integer, DataSetRow>();
    private SortCriteria sortCriteria;
    
    // *************
    // CONSTRUCTORS
    // *************
    
    /**
     * Default Constructor which creates an empty DataSet for the given definition and evaluationContext
     * @param definition
     * @param evaluationContext
     */
    public SimpleDataSet(DataSetDefinition definition, EvaluationContext evaluationContext) {
        this.definition = definition;
        this.context = evaluationContext;
    }
    
    // *************
    // INSTANCE METHODS
    // *************
    
    /**
     * Adds a row to this DataSet.  Also ensures all the Columns are added to the metadata
     * @param row the row to add to the DataSet
     */
    public void addRow(DataSetRow row) {
    	addRow(idToRowMap.size() + 1, row);
    }
    
    /**
     * Add the passed Row to the dataset at the passed index.  Also ensures all the Columns are added to the metadata
     */
    public void addRow(Integer index, DataSetRow row) {
    	idToRowMap.put(index, row);
    	if (row != null) {
	        for (DataSetColumn c : row.getColumnValues().keySet()) {
	        	if (getMetaData().getColumn(c.getName()) == null) {
	        		getMetaData().addColumn(c);
	        	}
	        }
    	}
    }
    
    /**
     * Adds a Column value to this DataSet
     * @param id the id of the object
     * @param column the name of the column
     * @param columnValue the value to add
     */
    public void addColumnValue(Integer id, DataSetColumn column, Object columnValue) {
    	DataSetRow row = idToRowMap.get(id);
    	if (row == null) {
    		row = new DataSetRow();
    		addRow(id, row);
    	}
    	row.addColumnValue(column, columnValue);
    	if (getMetaData().getColumn(column.getName()) == null) {
    		getMetaData().addColumn(column);
    	}
    }
    
    /**
     * Gets a Column value from this DataSet
     * @param id the id of the object
     * @param columnName the name of the column
     */
    public Object getColumnValue(Integer id, String columnName) {
    	DataSetRow row = idToRowMap.get(id);
    	if (row != null) {
    		return row.getColumnValue(columnName);
    	}
    	return null;
    }
    
	/**
     * @return the data
     */
    public DataSetRowList getRows() {
    	DataSetRowList l = new DataSetRowList();
    	l.addAll(idToRowMap.values());
    	if (getSortCriteria() != null) {
    		Collections.sort(l, new DataSetRowComparator(getSortCriteria()));
		}
    	return l;
    }

    /**
     * @see DataSet#iterator()
     */
    public Iterator<DataSetRow> iterator() {
        return getRows().iterator();
    }

    // *************
    // PROPERTY ACCESS
    // *************

	/**
	 * @return the definition
	 */
	public DataSetDefinition getDefinition() {
		return definition;
	}

	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(DataSetDefinition definition) {
		this.definition = definition;
	}

	/**
	 * @return the context
	 */
	public EvaluationContext getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(EvaluationContext context) {
		this.context = context;
	}

	/**
	 * @return the metaData
	 */
	public SimpleDataSetMetaData getMetaData() {
		return metaData;
	}

	/**
	 * @param metaData the metaData to set
	 */
	public void setMetaData(SimpleDataSetMetaData metaData) {
		this.metaData = metaData;
	}

	/**
	 * @return the SortCriteria
	 */
	public SortCriteria getSortCriteria() {
		return sortCriteria;
	}

	/**
	 * @param sortCriteria the SortCriteria to set
	 */
	public void setSortCriteria(SortCriteria sortCriteria) {
		this.sortCriteria = sortCriteria;
	}
}
