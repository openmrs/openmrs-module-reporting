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

import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * DataSet which is key-value pairs, instead of a full two-dimensional table
 */
public class MapDataSet extends SimpleDataSet {
	
	private static Integer SINGLE_ROW_ID = 1;
	
	//****** CONSTRUCTORS ******
	
    /**
     * Default Constructor which creates an empty DataSet for the given definition and evaluationContext
     * @param definition
     * @param evaluationContext
     */
    public MapDataSet(DataSetDefinition definition, EvaluationContext evaluationContext) {
    	super(definition, evaluationContext);
    }
    
    //****** INSTANCE METHODS ******
	
	/**
     * Adds a Data Element to this DataSet
     * @param key - The column key to add this element to
     * @param dataElement - The data to add
     */
    public void addData(DataSetColumn column, Object dataElement) {
    	addColumnValue(SINGLE_ROW_ID, column, dataElement);
    }
    
	/**
     * Gets the DataSetRow for this DataSet
     * @param dataElement - The data to add
     */
    public DataSetRow getData() {
    	if (getRows().isEmpty()) {
    		return null;
    	}
    	return getRows().get(0);
    }

	/**
     * Gets a Data Element from this DataSet
     * @param key - The column key to add this element to
     * @param dataElement - The data to add
     */
    public Object getData(DataSetColumn column) {
    	return getColumnValue(SINGLE_ROW_ID, column.getName());
    }
    
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getData().toString();
	}
}
