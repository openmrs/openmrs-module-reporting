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
package org.openmrs.module.reporting.dataset.definition.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.column.EvaluatedColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;
import org.openmrs.module.reporting.dataset.column.evaluator.ColumnEvaluator;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.util.HandlerUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of the DataSetDefinitionService.
 */
@Transactional
@Service
public class DataSetDefinitionServiceImpl extends BaseDefinitionService<DataSetDefinition> implements DataSetDefinitionService {

	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see DefinitionService#getDefinitionType()
	 */
	@Transactional(readOnly=true)
	public Class<DataSetDefinition> getDefinitionType() {
		return DataSetDefinition.class;
	}
	
	/**
	 * @see DataSetDefinitionService#evaluate(DataSetDefinition, EvaluationContext)
	 */
	@Transactional(readOnly=true)
	@Override
	public DataSet evaluate(DataSetDefinition definition, EvaluationContext context) throws EvaluationException {
		return (DataSet)super.evaluate(definition, context);
	}
	
	/** 
	 * @see DataSetDefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	@Transactional(readOnly=true)
	@Override
	public DataSet evaluate(Mapped<? extends DataSetDefinition> definition, EvaluationContext context) throws EvaluationException {
		return (DataSet)super.evaluate(definition, context);
	}
	
	/**
	 * @see DataSetDefinitionService#evaluateColumn(DataSetDefinition, EvaluationContext)
	 */
	public EvaluatedColumnDefinition evaluateColumn(ColumnDefinition definition, EvaluationContext context) throws APIException {
		ColumnEvaluator evaluator = HandlerUtil.getPreferredHandler(ColumnEvaluator.class, definition.getClass());
		if (evaluator == null) {
			throw new APIException("No ColumnEvaluator found for (" + definition.getClass() + ") " + definition.getName());
		}
		// Clone CohortDefinition and set all properties from the Parameters in the EvaluationContext
		ColumnDefinition clonedDefinition = DefinitionUtil.clone(definition);
		for (Parameter p : clonedDefinition.getParameters()) {
			Object value = p.getDefaultValue();
			if (context != null && context.containsParameter(p.getName())) {
				value = context.getParameterValue(p.getName());
			}
			ReflectionUtil.setPropertyValue(clonedDefinition, p.getName(), value);
		}
		return evaluator.evaluate(clonedDefinition, context);
	}
	
	/** 
	 * @see DataSetDefinitionService#evaluateColumn(Mapped, EvaluationContext)
	 */
	public EvaluatedColumnDefinition evaluateColumn(Mapped<? extends ColumnDefinition> definition, EvaluationContext context) throws APIException {
		EvaluationContext childContext = EvaluationContext.cloneForChild(context, definition);
		return evaluateColumn(definition.getParameterizable(), childContext);
	}
}
