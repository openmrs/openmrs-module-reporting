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
