/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.person.definition;

import org.openmrs.Person;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.query.BaseSqlQuery;

/**
 * SQL-based Person Query
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class SqlPersonQuery extends BaseSqlQuery<Person> implements PersonQuery {

    public static final long serialVersionUID = 1L;
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public SqlPersonQuery() {
		super();
	}
	
	/**
	 * 
	 * @param query the query to execute
	 */
	public SqlPersonQuery(String query) { 
		super(query);
	}
}
