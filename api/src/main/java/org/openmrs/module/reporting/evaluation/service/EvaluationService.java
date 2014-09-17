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
package org.openmrs.module.reporting.evaluation.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.QueryBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * DataSetEvaluation DAO Queries
 */
public interface EvaluationService extends OpenmrsService {

	/**
	 * Returns the columns that the query builder returns
	*/
	@Transactional(readOnly = true)
	public List<DataSetColumn> getColumns(QueryBuilder queryBuilder);

	/**
	 * Evaluates the passed QueryBuilder and returns the results as a List of Object[]
	 */
	@Transactional(readOnly = true)
	public List<Object[]> evaluateToList(QueryBuilder queryBuilder, EvaluationContext context);

	/**
	 * Evaluates the passed QueryBuilder and returns as a List of Objects of the passed type
	 * This requires a query that evaluates to exactly one column.  More than one column
	 * returned will result in an IllegalArgumentException
	 */
	@Transactional(readOnly = true)
	public <T> List<T> evaluateToList(QueryBuilder queryBuilder, Class<T> type, EvaluationContext context);

	/**
	 * Evaluates the passed QueryBuilder and returns as a Map of Objects of the passed types
	 * This requires a query that evaluates to exactly two columns.  More or less than two columns
	 * will result in an IllegalArgumentException
	 */
	@Transactional(readOnly = true)
	public <K, V> Map<K, V> evaluateToMap(QueryBuilder queryBuilder, Class<K> keyType, Class<V> valueType, EvaluationContext context);

	/**
	 * Evaluates the passed QueryBuilder and returns a single object result, throwing an exception if more than one row or more than one column is found
	 */
	@Transactional(readOnly = true)
	public <T> T evaluateToObject(QueryBuilder queryBuilder, Class<T> type, EvaluationContext context);
}