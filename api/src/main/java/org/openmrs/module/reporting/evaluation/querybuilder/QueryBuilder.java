/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.evaluation.querybuilder;

import org.hibernate.Query;
import org.openmrs.api.db.hibernate.DbSessionFactory;  
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

import java.util.List;

public interface QueryBuilder {

	/**
	 * @return the columns that this query will return
	 */
	public List<DataSetColumn> getColumns(DbSessionFactory sessionFactory);

	/**
	 * @return the results of evaluating this query
	 */
	public List<Object[]> evaluateToList(DbSessionFactory sessionFactory, EvaluationContext context);
}
