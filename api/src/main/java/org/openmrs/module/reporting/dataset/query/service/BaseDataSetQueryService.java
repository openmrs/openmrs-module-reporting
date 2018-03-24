/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset.query.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.reporting.dataset.query.service.db.DataSetQueryDAO;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Deprecated
public class BaseDataSetQueryService implements DataSetQueryService {

	protected static final Log log = LogFactory.getLog(BaseDataSetQueryService.class);

	//***** PROPERTIES *****
	
	private DataSetQueryDAO dao;
	
	//***** INSTANCE METHODS *****
	
	/** 
	 * @see DataSetQueryService#executeHqlQuery(String, Map<String, Object>)
	 */
	public List<Object> executeHqlQuery(String hqlQuery, Map<String, Object> parameterValues) {
		return dao.executeHqlQuery(hqlQuery, parameterValues);
	}
	
	/** 
	 * @see DataSetQueryDAO#getPropertyValues(Class, String, EvaluationContext)
	 */
	public Map<Integer, Object> getPropertyValues(Class<? extends OpenmrsObject> type, String property, EvaluationContext context) {
		return dao.getPropertyValues(type, property, context);
	}
	
	/** 
	 * @see DataSetQueryService#convertData(Class, String, Set, Class, String, Set)
	*/
	public Map<Integer, Integer> convertData(Class<?> fromType, String fromJoin, Set<Integer> fromIds, Class<?> toType, String toJoin, Set<Integer> toIds) {
		return dao.convertData(fromType, fromJoin, fromIds, toType, toJoin, toIds);
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the dao
	 */
	public DataSetQueryDAO getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(DataSetQueryDAO dao) {
		this.dao = dao;
	}
}
