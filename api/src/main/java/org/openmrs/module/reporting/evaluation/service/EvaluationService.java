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
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.QueryBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DataSetEvaluation DAO Queries
 */
public interface EvaluationService extends OpenmrsService {

	/**
	 * Evaluates the passed QueryBuilder and returns the results as a List of Object[]
	 */
	@Transactional
	public List<Object[]> evaluateToList(QueryBuilder queryBuilder);

	/**
	 * Evaluates the passed QueryBuilder and returns as a List of Objects of the passed type
	 * This requires a query that evaluates to exactly one column.  More than one column
	 * returned will result in an IllegalArgumentException
	 */
	@Transactional
	public <T> List<T> evaluateToList(QueryBuilder queryBuilder, Class<T> type);

	/**
	 * Evaluates the passed QueryBuilder and returns as a Map of Objects of the passed types
	 * This requires a query that evaluates to exactly two columns.  More or less than two columns
	 * will result in an IllegalArgumentException
	 */
	@Transactional
	public <K, V> Map<K, V> evaluateToMap(QueryBuilder queryBuilder, Class<K> keyType, Class<V> valueType);

	/**
	 * Get the key that can be used to uniquely reference this id set in temporary storage
	 */
	@Transactional
	public String generateKey(Set<Integer> ids);

	/**
	 * Indicate that you require joining against a particular set of ids, and that they
	 * should be made available to your calling code until you call the stopUsing method
	 * Returns the key that can be used to reference this id set at a later point in time
	 */
	@Transactional
	public String startUsing(Set<Integer> ids);

	/**
	 * Indicate that you require using the different base id sets contained in the passed EvaluationContext
	 */
	@Transactional
	public List<String> startUsing(EvaluationContext context);

	/**
	 * Returns true of an IdSet with the passed idSetKey is already persisted to temporary storage
	 */
	@Transactional
	public boolean isInUse(String idSetKey);

	/**
	 * Remove the passed idSet from temporary storage
	 */
	@Transactional
	public void stopUsing(String idSetKey);

	/**
	 * Indicate that you are finished using the different base id sets contained in the passed EvaluationContext
	 */
	@Transactional
	public void stopUsing(EvaluationContext context);

	/**
	 * Removes all persisted IdSets, for example as a sanity check whe
	 */
	@Transactional
	public void resetAllIdSets();
}