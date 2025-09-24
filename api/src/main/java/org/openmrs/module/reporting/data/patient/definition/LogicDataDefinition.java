/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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