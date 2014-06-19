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
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * Default implementation of the DataSetDefinitionService.
 */
public class DataSetDefinitionServiceImpl extends BaseDefinitionService<DataSetDefinition> implements DataSetDefinitionService {

	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see DefinitionService#getDefinitionType()
	 */
	public Class<DataSetDefinition> getDefinitionType() {
		return DataSetDefinition.class;
	}
	
	/**
	 * @see DataSetDefinitionService#evaluate(DataSetDefinition, EvaluationContext)
	 */
	@Override
	public DataSet evaluate(DataSetDefinition definition, EvaluationContext context) throws EvaluationException {
		return (DataSet)super.evaluate(definition, context);
	}
	
	/** 
	 * @see DataSetDefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	@Override
	public DataSet evaluate(Mapped<? extends DataSetDefinition> definition, EvaluationContext context) throws EvaluationException {
		return (DataSet)super.evaluate(definition, context);
	}
}
