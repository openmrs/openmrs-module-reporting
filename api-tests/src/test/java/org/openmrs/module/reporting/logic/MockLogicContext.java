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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.datasource.LogicDataSource;
import org.openmrs.logic.result.EmptyResult;
import org.openmrs.logic.result.Result;
import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * Mock implementation of LogicContext
 */
public class MockLogicContext implements LogicContext {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private Date indexDate = new Date();
	private Map<String, Object> globalParameters = new HashMap<String, Object>();
	private Cohort cohort;
	private Map<String, Map<Integer, Result>> cache = new HashMap<String, Map<Integer, Result>>();

	public MockLogicContext(Integer patientId) {
		cohort = new Cohort(patientId);
	}

	public MockLogicContext(Cohort patients) {
		cohort = patients;
	}
	
	/**
	 * @see LogicContext#getPatient(Integer)
	 */
	public Patient getPatient(Integer patientId) {
		return Context.getPatientService().getPatient(patientId);
	}
	
	/**
	 * @see LogicContext#eval(Integer, String)
	 */
	public Result eval(Integer patientId, String token) throws LogicException {
		return eval(patientId, token, null);
	}
	
	/**
	 * @see LogicContext#eval(Integer, String, Map)
	 */
	public Result eval(Integer patientId, String token, Map<String, Object> parameters) throws LogicException {
		return eval(patientId, new MockLogicCriteria(token), parameters);
	}
	
	/**
	 * @see LogicContext#eval(Integer, LogicCriteria, Map)
	 */
	public Result eval(Integer patientId, LogicCriteria criteria, Map<String, Object> parameters) throws LogicException {		
		Map<Integer, Result> resultMap = cache.get(criteria.getRootToken());
		if (resultMap == null) {
			Rule rule = Context.getLogicService().getRule(criteria.getRootToken());
			resultMap = new HashMap<Integer, Result>();
			for (Integer currPatientId : cohort.getMemberIds()) {
				Result r = rule.eval(this, currPatientId, parameters);
				resultMap.put(currPatientId, r);
			}
			cache.put(criteria.getRootToken(), resultMap);
		}
		return ObjectUtil.nvl(resultMap.get(patientId), new EmptyResult());
	}
	
	/**
	 * @see LogicContext#getLogicDataSource(String)
	 */
	public LogicDataSource getLogicDataSource(String name) {
		return Context.getLogicService().getLogicDataSource(name);
	}
	
	/**
	 * @see LogicContext#read(Integer, LogicDataSource, String)
	 */
	public Result read(Integer patientId, LogicDataSource dataSource, String key) throws LogicException {
		return read(patientId, dataSource, new MockLogicCriteria(key));
	}
	
	/**
	 * @see LogicContext#read(Integer, String)
	 */
	public Result read(Integer patientId, String key) throws LogicException {
		return read(patientId, null, key);
	}
	
	/**
	 * @see LogicContext#read(Integer, LogicCriteria)
	 */
	public Result read(Integer patientId, LogicCriteria criteria) throws LogicException {
		return read(patientId, null, criteria);
	}
	
	/**
	 * @see LogicContext#read(Integer, LogicDataSource, LogicCriteria)
	 */
	public Result read(Integer patientId, LogicDataSource dataSource, LogicCriteria criteria) throws LogicException {
		return eval(patientId, criteria, null);
	}
	
	/**
	 * @see LogicContext#setIndexDate(Date)
	 */
	public void setIndexDate(Date indexDate) {
		this.indexDate = indexDate;
	}
	
	/**
	 * @see LogicContext#getIndexDate()
	 */
	public Date getIndexDate() {
		return indexDate;
	}
	
	/**
	 * @see LogicContext#today()
	 */
	public Date today() {
		return indexDate;
	}
	
	/**
	 * @see LogicContext#setGlobalParameter(String, Object)
	 */
	public Object setGlobalParameter(String id, Object value) {
		return globalParameters.put(id, value);
	}
	
	/**
	 * @see LogicContext#getGlobalParameter(String)
	 */
	public Object getGlobalParameter(String id) {
		return globalParameters.get(id);
	}
	
	/**
	 * @see LogicContext#getGlobalParameters()
	 */
	public Collection<String> getGlobalParameters() {
		return globalParameters.keySet();
	}
}
