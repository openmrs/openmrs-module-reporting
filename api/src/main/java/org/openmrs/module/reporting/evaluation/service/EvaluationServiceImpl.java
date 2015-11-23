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

import org.hibernate.SessionFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.QueryBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the EvaluationService interface
 */
public class EvaluationServiceImpl extends BaseOpenmrsService implements EvaluationService {

	/**
	 * @see EvaluationService#getColumns(QueryBuilder)
	*/
	@Override
	@Transactional(readOnly = true)
	public List<DataSetColumn> getColumns(QueryBuilder queryBuilder) {
		return queryBuilder.getColumns(getSessionFactory());
	}

    /**
	 * @see EvaluationService#evaluateToList(QueryBuilder, EvaluationContext)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Object[]> evaluateToList(QueryBuilder queryBuilder, EvaluationContext context) {
		return queryBuilder.evaluateToList(getSessionFactory(), context);
	}

	/**
	 * @see EvaluationService#evaluateToList(QueryBuilder, Class, EvaluationContext)
	 */
	@Override
	@Transactional(readOnly = true)
	public <T> List<T> evaluateToList(QueryBuilder queryBuilder, Class<T> type, EvaluationContext context) {
		List<T> ret = new ArrayList<T>();
		List<Object[]> rawResults = evaluateToList(queryBuilder, context);
		for (Object[] resultRow : rawResults) {
			if (resultRow.length != 1) {
				throw new IllegalArgumentException("Unable to evaluate to a single value list. Exactly one column must be defined.");
			}
			ret.add((T)resultRow[0]);
		}
		return ret;
	}

	/**
	 * @see EvaluationService#evaluateToMap(QueryBuilder, Class, Class, EvaluationContext)
	 */
	@Override
	@Transactional(readOnly = true)
	public <K, V> Map<K, V> evaluateToMap(QueryBuilder queryBuilder, Class<K> keyType, Class<V> valueType, EvaluationContext context) {
		Map<K, V> ret = new HashMap<K, V>();
		List<Object[]> rawResults = evaluateToList(queryBuilder, context);
		for (Object[] resultRow : rawResults) {
			if (resultRow.length != 2) {
				throw new IllegalArgumentException("Unable to evaluate to a map. Exactly two columns must be defined.");
			}
			ret.put((K) resultRow[0], (V) resultRow[1]);
		}
		return ret;
	}

	@Override
	public <T> T evaluateToObject(QueryBuilder queryBuilder, Class<T> type, EvaluationContext context) {
		List<Object[]> rawResults = evaluateToList(queryBuilder, context);
		if (rawResults.size() != 1) {
			throw new IllegalArgumentException("Unable to evaluate to a single value. Exactly one row must be returned by query.");
		}
		Object[] row = rawResults.get(0);
		if (row.length != 1) {
			throw new IllegalArgumentException("Unable to evaluate to a single value. Exactly one column must be defined.");
		}
		return (T)row[0];
	}

	/**
	 * @return the sessionFactory
	 */
	private SessionFactory getSessionFactory() {
		return Context.getRegisteredComponents(SessionFactory.class).get(0);
	}
}