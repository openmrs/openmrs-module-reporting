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
package org.openmrs.module.reporting.data.patient.definition;

import org.openmrs.logic.result.Result;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyAndParameterCachingStrategy;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Logic-Based Data Definition
 */
@Deprecated
@Caching(strategy=ConfigurationPropertyAndParameterCachingStrategy.class)
@Localized("reporting.LogicDataDefinition")
public class LogicDataDefinition extends BaseDataDefinition implements PatientDataDefinition {
	
	public static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	@ConfigurationProperty
	private String logicQuery;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public LogicDataDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name only
	 */
	public LogicDataDefinition(String name) {
		super(name);
	}
	
	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
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