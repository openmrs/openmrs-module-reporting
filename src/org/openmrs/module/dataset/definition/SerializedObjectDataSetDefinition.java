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
package org.openmrs.module.dataset.definition;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.api.db.SerializedObject;
import org.openmrs.module.dataset.column.DataSetColumn;
import org.openmrs.module.reporting.definition.SerializedObjectDefinition;

public class SerializedObjectDataSetDefinition extends SerializedObjectDefinition implements DataSetDefinition {

    private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public SerializedObjectDataSetDefinition() {
		super();
	}
	
	/**
	 * Default Constructor
	 */
	public SerializedObjectDataSetDefinition(SerializedObject serializedObject) {
		super(serializedObject);
	}
	
	/**
	 * Gets a list of dataset columns.
	 * @return
	 */
	public List<DataSetColumn> getColumns() {
		return new ArrayList<DataSetColumn>();
	}
	
	/**
	 * Returns the {@link DataSetColumn} defined by the passed column key
	 * @param columnKey
	 * @return
	 */
	public DataSetColumn getColumn(String columnKey) {
		return null;
	}
}
