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
package org.openmrs.module.reporting.dataset.query.service.db;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * DataSetEvaluation DAO Queries
 */
@Deprecated
public interface DataSetQueryDAO {
	
	/** 
	 * @see DataSetQueryService#executeHqlQuery(String, Map<String, Object>)
	 */
	public List<Object> executeHqlQuery(String hqlQuery, Map<String, Object> parameterValues);
	
	/** 
	 * @see DataSetQueryService#getPropertyValues(Class, String, EvaluationContext)
	 */
	public Map<Integer, Object> getPropertyValues(Class<? extends OpenmrsObject> type, String property, EvaluationContext context);
	
	/** 
	 * @see DataSetQueryService#convertColumn()
	*/
	public Map<Integer, Integer> convertData(Class<?> fromType, String fromJoin, Set<Integer> fromIds, Class<?> toType, String toJoin, Set<Integer> toIds);
}
