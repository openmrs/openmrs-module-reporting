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
package org.openmrs.module.reporting.evaluation.querybuilder;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.module.reporting.dataset.DataSetColumn;

import java.util.List;

public interface QueryBuilder {

	/**
	 * @return the columns that this query will return
	 */
	public List<DataSetColumn> getColumns();

	/**
	 * Run the query and return the full result set as a List of Object[]
	 */
	public List<?> listResults(SessionFactory sessionFactory);
}
