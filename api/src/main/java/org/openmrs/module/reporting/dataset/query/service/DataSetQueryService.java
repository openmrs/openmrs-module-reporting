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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.transaction.annotation.Transactional;

/**
 * DataSetEvaluation DAO Queries
 */
@Deprecated
@Transactional(readOnly=true)
public interface DataSetQueryService {
	
	/** 
	 * @return the List of Objects that result from the passed hql query and parameters
	 */
	public List<Object> executeHqlQuery(String hqlQuery, Map<String, Object> parameterValues);
	
	/**
	 * @return all properties with the given name on the given type for the given ids
	 */
	public Map<Integer, Object> getPropertyValues(Class<? extends OpenmrsObject> type, String property, EvaluationContext context);

	/** 
	 * @return a Set<Integer> given passed data
	*/	 
	public Map<Integer, Integer> convertData(Class<?> fromType, String fromJoin, Set<Integer> fromIds, Class<?> toType, String toJoin, Set<Integer> toIds);
}
