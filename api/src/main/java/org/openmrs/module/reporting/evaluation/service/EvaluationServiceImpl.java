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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationProfiler;
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

	private transient Log log = LogFactory.getLog(this.getClass());

    /**
	 * @see EvaluationService#evaluateToList(QueryBuilder, EvaluationContext)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Object[]> evaluateToList(QueryBuilder queryBuilder, EvaluationContext context) {
		List<Object[]> ret = new ArrayList<Object[]>();
		for (Object resultRow : listResults(queryBuilder, context)) {
			if (resultRow instanceof Object[]) {
				ret.add((Object[])resultRow);
			}
			else {
				ret.add(new Object[]{resultRow});
			}
		}
		return ret;
	}

	/**
	 * @see EvaluationService#evaluateToList(QueryBuilder, Class, EvaluationContext)
	 */
	@Override
	@Transactional(readOnly = true)
	public <T> List<T> evaluateToList(QueryBuilder queryBuilder, Class<T> type, EvaluationContext context) {
		List<T> ret = new ArrayList<T>();
		for (Object resultRow : listResults(queryBuilder, context)) {
			if (resultRow instanceof Object[]) {
				throw new IllegalArgumentException("Unable to evaluate to a single value list. Exactly one column must be defined.");
			}
			ret.add((T)resultRow);
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
		for (Object resultRow : listResults(queryBuilder, context)) {
			boolean found = false;
			if (resultRow instanceof Object[]) {
				Object[] results = (Object[])resultRow;
				if (results.length == 2) {
					ret.put((K)results[0], (V)results[1]);
					found = true;
				}
			}
			if (!found) {
				throw new IllegalArgumentException("Unable to evaluate to a map. Exactly two columns must be defined.");
			}
		}
		return ret;
	}

	/**
	 * Convenience method for executing a query and timing the execution
	 */
	protected List listResults(QueryBuilder qb, EvaluationContext context) {

		List ret;

		// Due to hibernate bug HHH-2166, we need to make sure the HqlSqlWalker logger is not at DEBUG or TRACE level
		Logger hqlSqlWalkerLogger = LogManager.getLogger("org.hibernate.hql.ast.HqlSqlWalker");
		Level hqlSqlWalkerLoggerStartingLevel = hqlSqlWalkerLogger.getLevel();
		Level hqlSqlWalkerEffectiveLevel = hqlSqlWalkerLogger.getEffectiveLevel();
		if (hqlSqlWalkerEffectiveLevel == Level.TRACE || hqlSqlWalkerEffectiveLevel == Level.DEBUG) {
			hqlSqlWalkerLogger.setLevel(Level.INFO);
		}

		// Build the query, and profile how long it takes to execute
		Query query = qb.buildQuery(getSessionFactory());
		EvaluationProfiler profiler = new EvaluationProfiler(context);
		profiler.logBefore("EXECUTING_QUERY", qb.toString());
		try {
			ret = query.list();
		}
		catch (RuntimeException e) {
			profiler.logError("EXECUTING_QUERY", e);
			throw e;
		}
		profiler.logAfter("EXECUTING_QUERY", "Completed successfully with " + ret.size() + " results");

		// Reset the log level if needed
		hqlSqlWalkerLogger.setLevel(hqlSqlWalkerLoggerStartingLevel);

		return ret;
	}

	/**
	 * @return the sessionFactory
	 */
	private SessionFactory getSessionFactory() {
		return Context.getRegisteredComponents(SessionFactory.class).get(0);
	}
}