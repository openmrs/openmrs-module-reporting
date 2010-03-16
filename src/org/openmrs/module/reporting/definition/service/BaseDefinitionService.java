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
package org.openmrs.module.reporting.definition.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 *  Base Implementation of the DefinitionService API
 */
@Transactional
public abstract class BaseDefinitionService<T extends Definition> extends BaseOpenmrsService implements DefinitionService<T> {
	
	protected static Log log = LogFactory.getLog(BaseDefinitionService.class);
	
	/**
	 * @see DefinitionService#getDefinition(String, Class)
	 */
	public T getDefinition(String uuid, Class<? extends T> type) {
	   	T ret = null;
    	if (StringUtils.hasText(uuid)) {
	    	ret = getDefinitionByUuid(uuid);
    	}
    	else if (type != null) {
     		try {
    			ret = type.newInstance();
    		}
    		catch (Exception e) {
    			log.error("Exception occurred while instantiating definition of type " + type, e);
    			throw new IllegalArgumentException("Unable to instantiate a Definition of type: " + type, e);
    		}
    	}
    	else {
    		throw new IllegalArgumentException("You must supply either a uuid or a type");
    	}
    	return ret;
	}
	
	/**
	 * Default implementation is to consider a Definition to contain a tag if the tag is part of the Definition name
	 * @see DefinitionService#getDefinitionsByTag(String)
	 */
	public List<T> getDefinitionsByTag(String tagName) {
		return getDefinitions(tagName, false);
	}

	/**
	 * @see DefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	public Evaluated<T> evaluate(Mapped<? extends T> definition, EvaluationContext context) throws APIException {
		EvaluationContext childContext = EvaluationContext.cloneForChild(context, definition);
		log.debug("Evaluating: " + definition.getParameterizable() + "(" + context.getParameterValues() + ")");
		return evaluate(definition.getParameterizable(), childContext);
	}
}
