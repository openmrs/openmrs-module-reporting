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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.QueryBuilder;
import org.openmrs.module.reporting.query.IdSet;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the EvaluationService interface
 */
public class EvaluationServiceImpl extends BaseOpenmrsService implements EvaluationService {

	private transient Log log = LogFactory.getLog(this.getClass());
	private List<String> currentIdSetKeys = Collections.synchronizedList(new ArrayList<String>());

	/**
	 * @see EvaluationService#evaluateToList(QueryBuilder)
	 */
	@Override
	public List<Object[]> evaluateToList(QueryBuilder queryBuilder) {
		List<Object[]> ret = new ArrayList<Object[]>();
		for (Object resultRow : queryBuilder.listResults(getSessionFactory())) {
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
	 * @see EvaluationService#evaluateToList(QueryBuilder, Class)
	 */
	@Override
	public <T> List<T> evaluateToList(QueryBuilder queryBuilder, Class<T> type) {
		List<T> ret = new ArrayList<T>();
		for (Object resultRow : queryBuilder.listResults(getSessionFactory())) {
			if (resultRow instanceof Object[]) {
				throw new IllegalArgumentException("Unable to evaluate to a single value list. Exactly one column must be defined.");
			}
			ret.add((T)resultRow);
		}
		return ret;
	}

	/**
	 * @see EvaluationService#evaluateToMap(QueryBuilder, Class, Class)
	 */
	@Override
	public <K, V> Map<K, V> evaluateToMap(QueryBuilder queryBuilder, Class<K> keyType, Class<V> valueType) {
		Map<K, V> ret = new HashMap<K, V>();
		for (Object resultRow : queryBuilder.listResults(getSessionFactory())) {
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
	 * @see EvaluationService#generateKey(Set)
	 */
	@Override
	public String generateKey(Set<Integer> ids) {
		List<Integer> l = new ArrayList<Integer>(ids);
		Collections.sort(l);
		return DigestUtils.shaHex(l.toString());
	}

	/**
	 * @see EvaluationService#startUsing(Set)
	 */
	@Transactional
	@Override
	public String startUsing(Set<Integer> ids) {
		if (ids == null || ids.isEmpty()) {
			return null;
		}
		String idSetKey = generateKey(ids);
		if (isInUse(idSetKey)) {
			log.debug("Attempting to persist an IdSet that has previously been persisted.  Using existing values.");
			// TODO: As an additional check here, we could confirm that they are the same by loading into memory
		}
		else {
			StringBuilder q = new StringBuilder();
			q.append("insert into reporting_idset (idset_key, member_id) values ");
			for (Iterator<Integer> i = ids.iterator(); i.hasNext(); ) {
				Integer id = i.next();
				q.append("('").append(idSetKey).append("',").append(id).append(")").append(i.hasNext() ? "," : "");
			}
			executeUpdate(q.toString());
			log.debug("Persisted idset: " + idSetKey + "; size: " + ids.size() + "; total active: " + currentIdSetKeys.size());
		}
		currentIdSetKeys.add(idSetKey);
		return idSetKey;
	}

	/**
	 * @see EvaluationService#startUsing(EvaluationContext)
	 */
	@Transactional
	@Override
	public List<String> startUsing(EvaluationContext context) {
		List<String> idSetsAdded = new ArrayList<String>();
		for (IdSet<?> idSet : context.getAllBaseIdSets().values()) {
			if (idSet != null && !idSet.getMemberIds().isEmpty()) {
				idSetsAdded.add(startUsing(idSet.getMemberIds()));
			}
		}
		return idSetsAdded;
	}

	/**
	 * @see EvaluationService#isInUse(String)
	 */
	@Override
	public boolean isInUse(String idSetKey) {
		String existQuery = "select count(*) from reporting_idset where idset_key = '"+idSetKey+"'";
		String check = executeUniqueResult(existQuery).toString();
		return !check.equals("0");
	}

	/**
	 * @see EvaluationService#stopUsing(String)
	 */
	@Transactional
	@Override
	public void stopUsing(String idSetKey) {
		int indexToRemove = currentIdSetKeys.lastIndexOf(idSetKey);
		currentIdSetKeys.remove(indexToRemove);
		if (!currentIdSetKeys.contains(idSetKey)) {
			executeUpdate("delete from reporting_idset where idset_key = '" + idSetKey + "'");
			currentIdSetKeys.remove(idSetKey);
			log.debug("Deleted idset: " + idSetKey + "; total active: " + currentIdSetKeys.size());
		}
	}

	/**
	 * @see EvaluationService#stopUsing(EvaluationContext)
	 */
	@Transactional
	@Override
	public void stopUsing(EvaluationContext context) {
		for (IdSet<?> idSet : context.getAllBaseIdSets().values()) {
			if (idSet != null && !idSet.getMemberIds().isEmpty()) {
				stopUsing(generateKey(idSet.getMemberIds()));
			}
		}
	}

	/**
	 * @see EvaluationService#resetAllIdSets()
	 */
	@Transactional
	@Override
	public void resetAllIdSets() {
		currentIdSetKeys.clear();
		executeUpdate("delete from reporting_idset");

	}

	private Object executeUniqueResult(String sql) {
		Query query = getSessionFactory().getCurrentSession().createSQLQuery(sql);
		return query.uniqueResult();
	}

	private void executeUpdate(String sql) {
		Query query = getSessionFactory().getCurrentSession().createSQLQuery(sql);
		query.executeUpdate();
	}

	private SessionFactory getSessionFactory() {
		return Context.getRegisteredComponents(SessionFactory.class).get(0);
	}
}