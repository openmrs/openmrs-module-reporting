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
package org.openmrs.module.reporting.dataset.column.definition.patient;

import org.openmrs.OpenmrsData;
import org.openmrs.Patient;
import org.openmrs.logic.result.Result;
import org.openmrs.module.reporting.dataset.column.definition.BaseColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;
import org.openmrs.module.reporting.dataset.column.converter.ColumnConverter;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Logic Column
 */
public class LogicColumnDefinition extends BaseColumnDefinition implements PatientColumnDefinition {
	
	public static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty
	private String logicQuery;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public LogicColumnDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name only
	 */
	public LogicColumnDefinition(String name) {
		super(name);
	}
	
	/**
	 * Constructor to populate name and transform
	 */
	public LogicColumnDefinition(String name, ColumnConverter transform) {
		super(name, null, transform);
	}
	
	//***** INSTANCE METHODS *****
	
	/** 
	 * @see RowPerObjectColumnDefinition#getBaseType()
	 */
	public Class<? extends OpenmrsData> getBaseType() {
		return Patient.class;
	}

	/** 
	 * @see RowPerObjectColumnDefinition#getIdProperty()
	 */
	public String getIdProperty() {
		return "patientId";
	}

	/** 
	 * @see BaseColumnDefinition#getRawDataType()
	 */
	@Override
	public Class<?> getRawDataType() {
		return Result.class;
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the logicQuery
	 */
	public String getLogicQuery() {
		return logicQuery;
	}

	/**
	 * @param logicQuery the logicQuery to set
	 */
	public void setLogicQuery(String logicQuery) {
		this.logicQuery = logicQuery;
	}
}