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
