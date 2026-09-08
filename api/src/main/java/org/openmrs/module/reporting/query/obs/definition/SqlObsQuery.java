/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.obs.definition;

import org.openmrs.Obs;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.query.BaseSqlQuery;

/**
 * SQL-based Obs Query
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class SqlObsQuery extends BaseSqlQuery<Obs> implements ObsQuery {

    public static final long serialVersionUID = 1L;
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public SqlObsQuery() {
		super();
	}
	
	/**
	 * 
	 * @param query the query to initialize
	 */
	public SqlObsQuery(String query) { 
		super(query);
	}
}
