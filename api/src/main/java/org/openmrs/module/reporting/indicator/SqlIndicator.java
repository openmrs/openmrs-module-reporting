/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
