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
