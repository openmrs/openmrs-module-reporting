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

import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class SqlCohortDefinition extends BaseCohortDefinition {

    private static final long serialVersionUID = 1L;
	
	@ConfigurationProperty(required=false)
	private String sqlQuery;
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public SqlCohortDefinition() {
		super();
	}
	
	public SqlCohortDefinition(String sqlQuery) { 
		super();
		this.sqlQuery = sqlQuery;

	}
	
	//***** INSTANCE METHODS *****
	
	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer("");
		if (sqlQuery != null) { 
			buffer.append(sqlQuery);
		}		
		return buffer.toString();
	}



  
}
