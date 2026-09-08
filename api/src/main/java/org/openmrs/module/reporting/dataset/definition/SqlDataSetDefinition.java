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
