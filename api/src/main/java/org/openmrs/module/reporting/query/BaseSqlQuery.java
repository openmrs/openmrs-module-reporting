/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

/**
 * Abstract SQL-based Query
 */
public abstract class BaseSqlQuery<T extends OpenmrsObject> extends BaseQuery<T> {

    public static final long serialVersionUID = 1L;

	@ConfigurationProperty(required=true)
	private String query;

	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public BaseSqlQuery() {
		super();
	}

	/**
	 * @param query the query to execute
	 */
	public BaseSqlQuery(String query) {
		this.query = query;
	}

	//***** INSTANCE METHODS *****

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return getClass().getSimpleName() + ": [" + ObjectUtil.nvlStr(query, "") + "]";
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}
}
