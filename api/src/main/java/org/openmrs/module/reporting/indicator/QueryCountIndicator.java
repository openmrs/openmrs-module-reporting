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
package org.openmrs.module.reporting.indicator;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.Query;

/**
 * Query-based indicator
 */
@Localized("reporting.QueryCountIndicator")
public class QueryCountIndicator extends BaseIndicator {

    //***** PROPERTIES *****

    @ConfigurationProperty
    private Mapped<? extends Query> query;

    //***** CONSTRUCTORS *****

    /**
     * Default Constructor
     */
    public QueryCountIndicator() { }
    
    //***** Property Access *****


	public Mapped<? extends Query> getQuery() {
		return query;
	}

	public void setQuery(Mapped<? extends Query> query) {
		this.query = query;
	}
}