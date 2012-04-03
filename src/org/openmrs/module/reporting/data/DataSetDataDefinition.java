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

import java.util.List;

import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.RowPerObjectDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.BaseDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

/**
 * DataSet Data Definition
 */
public abstract class DataSetDataDefinition extends BaseDataDefinition {
	
	public static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
	private RowPerObjectDataSetDefinition definition;
	
	@ConfigurationProperty(required=true)
	private TimeQualifier whichValues;
	
	@ConfigurationProperty(required=true)
	private Integer numberOfValues;
	
	//***** CONSTRUCTORS *****
		
	/**
	 * Default Constructor
	 */
	public DataSetDataDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate definition only
	 */
	public DataSetDataDefinition(RowPerObjectDataSetDefinition definition) {
		this();
		setDefinition(definition);
	}
	
	/**
	 * Constructor to populate all properties
	 */
	public DataSetDataDefinition(RowPerObjectDataSetDefinition definition, TimeQualifier whichValues, Integer numberOfValues) {
		this(definition);
		this.whichValues = whichValues;
		this.numberOfValues = numberOfValues;
	}

	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		if (numberOfValues != null && numberOfValues == 1) {
			return DataSetRow.class;
		}
		return List.class;
	}
	
	/**
	 * @see BaseDefinition#getParameter(String)
	 */
	@Override
	public Parameter getParameter(String name) {
		return definition.getParameter(name);
	}

	/**
	 * @see BaseDefinition#getParameters()
	 */
	@Override
	public List<Parameter> getParameters() {
		return definition.getParameters();
	}

	//***** PROPERTY ACCESS *****
	
	/**
	 * @return the definition
	 */
	public RowPerObjectDataSetDefinition getDefinition() {
		return definition;
	}

	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(RowPerObjectDataSetDefinition definition) {
		this.definition = definition;
	}

	/**
	 * @return the whichValues
	 */
	public TimeQualifier getWhichValues() {
		return whichValues;
	}

	/**
	 * @param whichValues the whichValues to set
	 */
	public void setWhichValues(TimeQualifier whichValues) {
		this.whichValues = whichValues;
	}

	/**
	 * @return the numberOfValues
	 */
	public Integer getNumberOfValues() {
		return numberOfValues;
	}

	/**
	 * @param numberOfValues the numberOfValues to set
	 */
	public void setNumberOfValues(Integer numberOfValues) {
		this.numberOfValues = numberOfValues;
	}
}