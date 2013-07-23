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
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyAndParameterCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.indicator.evaluator.SqlIndicatorEvaluator;

/**
 * This is a SQL-based indicator definition that lets you push calculation of an indicator down to the database level.  
 * This implementation requires that your sql and denominatorSQL return single values.
 * If you're not doing a Fraction indicator, it is fine to leave the denominator null -- The evaluator will return the return value for the sql property as the indicator value.
 * And if you're using denominatorSQL, please have your numerator and denominator return whole numbers.
 * @see SqlIndicatorEvaluator
 *
 */
@Caching(strategy=ConfigurationPropertyAndParameterCachingStrategy.class)
@Localized("reporting.SqlIndicator")
public class SqlIndicator extends BaseIndicator {

	public static final long serialVersionUID = 1L;
	
	@ConfigurationProperty
	private String sql;
	
	@ConfigurationProperty
	private String denominatorSql;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getDenominatorSql() {
		return denominatorSql;
	}

	public void setDenominatorSql(String denominatorSql) {
		this.denominatorSql = denominatorSql;
	}
}
