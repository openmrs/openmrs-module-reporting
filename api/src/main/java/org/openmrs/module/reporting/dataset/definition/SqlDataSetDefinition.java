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

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.dataset.definition.evaluator.SqlDataSetEvaluator;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyAndParameterCachingStrategy;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Definition of a SQL DataSet
 * @see SqlDataSetEvaluator
 */
@Caching(strategy=ConfigurationPropertyAndParameterCachingStrategy.class)
@Localized("reporting.SqlDataSetDefinition")
public class SqlDataSetDefinition extends BaseDataSetDefinition {

	public static final long serialVersionUID = 6405583324151111487L;

	//***** PROPERTIES *****
	
	@ConfigurationProperty(required=true)
	private String sqlQuery;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Constructor
	 */
	public SqlDataSetDefinition() {
		super();
	}
	
	/**
	 * Public constructor
	 * 
	 * @param name
	 * @param description
	 * @param sqlQuery
	 */
	public SqlDataSetDefinition(String name, String description, String sqlQuery) {
		this();
		this.setName(name);
		this.setDescription(description);
		this.setSqlQuery(sqlQuery);
	}

	//***** PROPERTY ACCESS *****
	
	/**
	 * @return the sqlQuery
	 */
	public String getSqlQuery() {
		return sqlQuery;
	}

	/**
	 * @param sqlQuery the sqlQuery to set
	 */
	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}	
}
