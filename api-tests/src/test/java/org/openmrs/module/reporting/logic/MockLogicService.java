/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.datasource.LogicDataSource;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.springframework.stereotype.Service;

/**
 * Mock implementation of Logic Service to get around issues using the actual Logic Service implementations
 */
@Service(value="logicService")
public class MockLogicService implements LogicService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private Map<String, Rule> rules = new HashMap<String, Rule>();
		
	/**
	 * Default constructor
	 */
	public MockLogicService() {
		rules.put("gender", new GenderRule());
	}
		
	/**
	 * @see LogicService#getTokens()
	 */
	public Set<String> getTokens() {
		return rules.keySet();
	}
	
	/**
	 * @see LogicService#getAllTokens()
	 */
	public List<String> getAllTokens() {
		return new ArrayList<String>(getTokens());
	}
	
	/**
	 * @see LogicService#findToken(String)
	 */
	public Set<String> findToken(String token) {
		Set<String> tokens = new HashSet<String>();
		tokens.addAll(getTokens(token));
		return tokens;
	}
	
	/**
	 * @see LogicService#getTokens(String)
	 */
	public List<String> getTokens(String partialToken) {
		List<String> ret = new ArrayList<String>();
		for (String token : getTokens()) {
			if (token.toLowerCase().trim().contains(partialToken.toLowerCase().trim())) {
				ret.add(token);
			}
		}
		return ret;
	}
	
	/**
	 * @see LogicService#addRule(String, Rule)
	 */
	public void addRule(String token, Rule rule) throws LogicException {
		rules.put(token, rule);
	}
	
	/**
	 * @see LogicService#getRule(String)
	 * @should return ReferenceRule when the token are already registered
	 * @should return new ReferenceRule when the special string token are passed
	 * @should return Rule when concept derived name are passed
	 * @should return Rule when registered concept derived name are passed
	 */
	public Rule getRule(String token) throws LogicException {
		return rules.get(token);
	}
	
	/**
	 * @see LogicService#updateRule(String, Rule)
	 */
	public void updateRule(String token, Rule rule) throws LogicException {
		addRule(token, rule);
	}
	
	/**
	 * @see LogicService#removeRule(String)
	 */
	public void removeRule(String token) throws LogicException {
		rules.remove(token);
	}
	
	/**
	 * @see LogicService#eval(Integer, String)
	 */
	public Result eval(Integer patientId, String expression) throws LogicException {
		return eval(patientId, parse(expression));
	}
	
	/**
	 * @see LogicService#eval(Integer, String, java.util.Map)
	 */
	public Result eval(Integer patientId, String expression, Map<String, Object> params) throws LogicException {
		LogicCriteria criteria = parse(expression);
		criteria.setLogicParameters(params);
		return eval(patientId, criteria);
	}
	
	/**
	 * @see LogicService#eval(Integer, java.util.Map,
	 *      String[])
	 */
	public Map<String, Result> eval(Integer patientId, Map<String, Object> parameters, String... expressions) throws LogicException {
		LogicContext context = new MockLogicContext(patientId);
		Map<String, Result> ret = new LinkedHashMap<String, Result>();
		for (int i = 0; i < expressions.length; ++i) {
			String expr = expressions[i];
			LogicCriteria criteria = parse(expr);
			ret.put(expr, context.eval(patientId, criteria, parameters));
		}
		return ret;
	}
	
	/**
	 * @see LogicService#eval(Integer, java.util.Map, LogicCriteria[])
	 */
	public Map<LogicCriteria, Result> eval(Integer patientId, Map<String, Object> parameters, LogicCriteria... criteria) throws LogicException {
		LogicContext context = new MockLogicContext(patientId);
		
		Map<LogicCriteria, Result> ret = new LinkedHashMap<LogicCriteria, Result>();
		for (int i = 0; i < criteria.length; ++i) {
			LogicCriteria criterion = criteria[i];
			ret.put(criterion, context.eval(patientId, criterion, parameters));
		}
		return ret;
	}
	
	/**
	 * @see LogicService#eval(Integer, LogicCriteria)
	 */
	public Result eval(Integer patientId, LogicCriteria criteria) throws LogicException {
		return eval(patientId, criteria, criteria.getLogicParameters());
	}
	
	/**
	 * @see LogicService#eval(Integer, LogicCriteria,
	 *      java.util.Map)
	 */
	public Result eval(Integer patientId, LogicCriteria criteria, Map<String, Object> parameters) throws LogicException {
		LogicContext context = new MockLogicContext(patientId);
		Result result = context.eval(patientId, criteria, parameters);
		context = null;
		return result;
	}
	
	/**
	 * @see LogicService#eval(org.openmrs.Patient, String)
	 */
	public Result eval(Patient who, String expression) throws LogicException {
		return eval(who.getPatientId(), expression);
	}
	
	/**
	 * @see LogicService#eval(Patient, String, Map)
	 */
	public Result eval(Patient who, String expression, Map<String, Object> parameters) throws LogicException {
		return eval(who.getPatientId(), expression, parameters);
	}
	
	/**
	 * @see LogicService#eval(Patient, LogicCriteriaImpl)
	 */
	public Result eval(Patient who, LogicCriteria criteria) throws LogicException {
		return eval(who.getPatientId(), criteria);
	}
	
	/**
	 * @see LogicService#eval(Patient, LogicCriteria, Map)
	 */
	public Result eval(Patient who, LogicCriteria criteria, Map<String, Object> parameters) throws LogicException {
		return eval(who.getPatientId(), criteria, parameters);
	}
	
	/**
	 * @see LogicService#eval(org.openmrs.Cohort, String)
	 */
	public Map<Integer, Result> eval(Cohort who, String expression) throws LogicException {
		return eval(who, parse(expression));
	}
	
	/**
	 * @see LogicService#eval(org.openmrs.Cohort, String, java.util.Map)
	 */
	public Map<Integer, Result> eval(Cohort who, String expression, Map<String, Object> parameters) throws LogicException {
		LogicCriteria criteria = parse(expression);
		criteria.setLogicParameters(parameters);
		return eval(who, criteria);
	}
	
	/**
	 * @see LogicService#eval(org.openmrs.Cohort, LogicCriteria)
	 */
	public Map<Integer, Result> eval(Cohort who, LogicCriteria criteria) throws LogicException {
		return eval(who, criteria, criteria.getLogicParameters());
	}
	
	/**
	 * @see LogicService#eval(org.openmrs.Cohort, LogicCriteria,
	 *      java.util.Map)
	 */
	public Map<Integer, Result> eval(Cohort who, LogicCriteria criteria, Map<String, Object> parameters) throws LogicException {
		LogicContext context = new MockLogicContext(who);
		Map<Integer, Result> resultMap = new Hashtable<Integer, Result>();
		for (Integer pid : who.getMemberIds()) {
			resultMap.put(pid, context.eval(pid, criteria, parameters));
		}
		context = null;
		return resultMap;
	}
	
	/**
	 * @see LogicService#eval(org.openmrs.Cohort, java.util.List)
	 */
	public Map<LogicCriteria, Map<Integer, Result>> eval(Cohort patients, List<LogicCriteria> criterias)
	                                                                                                    throws LogicException {
		Map<LogicCriteria, Map<Integer, Result>> result = new HashMap<LogicCriteria, Map<Integer, Result>>();
		
		for (LogicCriteria criteria : criterias) {
			result.put(criteria, eval(patients, criteria));
		}
		
		return result;
	}
	
	/**
	 * @see LogicService#addRule(String, String[], Rule)
	 */
	public void addRule(String token, String[] tags, Rule rule) throws LogicException {
		throw new UnsupportedOperationException("Use TokenService.registerToken and manually add tags");
	}
	
	/**
	 * @see LogicService#addTokenTag(String, String)
	 */
	public void addTokenTag(String token, String tag) {
	}
	
	/**
	 * @see LogicService#findTags(String)
	 */
	public Set<String> findTags(String partialTag) {
		return new HashSet<String>();
	}
	
	/**
	 * @see LogicService#getTags(String)
	 */
	public List<String> getTags(String partialTag) {
		return new ArrayList<String>(findTags(partialTag));
	}
	
	/**
	 * @see LogicService#getTagsByToken(String)
	 */
	public Collection<String> getTagsByToken(String token) {
		return findTags(token);
	}
	
	/**
	 * @see LogicService#getTokenTags(String)
	 */
	public Set<String> getTokenTags(String token) {
		return findTags(token);
	}
	
	/**
	 * @see LogicService#getTokensByTag(String)
	 */
	public Set<String> getTokensByTag(String tag) {
		return findTags(tag);
	}
	
	/**
	 * @see LogicService#getTokensWithTag(String)
	 */
	public List<String> getTokensWithTag(String tag) {
		return getTags(tag);
	}
	
	/**
	 * @see LogicService#removeTokenTag(String, String)
	 */
	public void removeTokenTag(String token, String tag) {
	}
	
	/**
	 * @see LogicService#getDefaultDatatype(String)
	 */
	public Datatype getDefaultDatatype(String token) {
		return getRule(token).getDefaultDatatype();
	}
	
	public Set<RuleParameterInfo> getParameterList(String token) {
		return new HashSet<RuleParameterInfo>();
	}
	
	/**
	 * @deprecated data sources are now auto-registered via Spring
	 * @see LogicService#registerLogicDataSource(String, LogicDataSource)
	 */
	public void registerLogicDataSource(String name, LogicDataSource dataSource) throws LogicException {
		// do nothing
	}
	
	/**
	 * @see LogicService#getLogicDataSource(String)
	 */
	public LogicDataSource getLogicDataSource(String name) {
		return getLogicDataSources().get(name);
	}
	
	/**
	 * @see LogicService#getLogicDataSources()
	 */
	public Map<String, LogicDataSource> getLogicDataSources() {
		return new HashMap<String, LogicDataSource>();
	}
	
	/**
	 * @see LogicService#parseString(String)
	 */
	public LogicCriteria parseString(String inStr) {
		return parse(inStr);
	}
	
	/**
	 * @see LogicService#parse(String)
	 */
	public LogicCriteria parse(String criteria) {
		return new MockLogicCriteria(criteria);
	}

	public void removeLogicDataSource(String arg0) {
	}

	public void setLogicDataSources(Map<String, LogicDataSource> arg0) throws LogicException {
	}
}
