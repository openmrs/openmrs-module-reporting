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

import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Abstract SQL-based Data Definition
 */
public abstract class BaseSqlDataDefinition extends BaseDataDefinition {

    public static final long serialVersionUID = 1L;

	@ConfigurationProperty(required=true)
	private String sql;

	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public BaseSqlDataDefinition() {
		super();
	}

	/**
	 * @param sql the query to execute
	 */
	public BaseSqlDataDefinition(String sql) {
		this.sql = sql;
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return getClass().getSimpleName() + ": [" + ObjectUtil.nvlStr(sql, "") + "]";
	}
	
	//***** PROPERTY ACCESS *****

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    // These are here for compatibility

    public String getQuery() {
        return getSql();
    }

    public void setQuery(String query) {
        setSql(query);
    }
}
