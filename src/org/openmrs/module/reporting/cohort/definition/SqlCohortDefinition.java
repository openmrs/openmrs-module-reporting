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
package org.openmrs.module.reporting.cohort.definition;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.query.definition.QueryDefinition;
import org.openmrs.module.reporting.query.definition.SqlQueryDefinition;

/**
 * SQL-based Cohort Definition
 */
@Localized("reporting.SqlCohortDefinition")
public class SqlCohortDefinition extends BaseCohortDefinition {

    private static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(required=true)
	private QueryDefinition queryDefinition;
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public SqlCohortDefinition() {
		super();
	}
	
	/**
	 * 
	 * @param sqlQuery
	 */
	public SqlCohortDefinition(String sqlQuery) { 
		super();
		this.queryDefinition = new SqlQueryDefinition(sqlQuery);
	}

	//***** INSTANCE METHODS *****

	/**
	 * @return the queryDefinition
	 */
	public QueryDefinition getQueryDefinition() {
		return queryDefinition;
	}

	/**
	 * @param queryDefinition the queryDefinition to set
	 */
	public void setQueryDefinition(QueryDefinition queryDefinition) {
		this.queryDefinition = queryDefinition;
	}	
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer("SQL Cohort Query: [");
		if (queryDefinition != null && queryDefinition.getQueryString() != null) { 
			buffer.append(queryDefinition.getQueryString());
		}		
		buffer.append("]");
		return buffer.toString();
	}
  
}
