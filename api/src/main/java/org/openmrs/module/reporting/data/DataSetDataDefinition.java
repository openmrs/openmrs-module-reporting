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