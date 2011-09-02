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

import org.openmrs.api.APIException;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.column.EvaluatedColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.transaction.annotation.Transactional;

/**
 *  DataSetService
 */
@Transactional
public interface DataSetDefinitionService extends DefinitionService<DataSetDefinition> {
	
	/**
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
	@Transactional(readOnly = true)
	public DataSet evaluate(DataSetDefinition definition, EvaluationContext context) throws EvaluationException;
	
	/**
	 * @see DefinitionService#evaluate(Mapped<Definition>, EvaluationContext)
	 */
	@Transactional(readOnly = true)
	public DataSet evaluate(Mapped<? extends DataSetDefinition> definition, EvaluationContext context) throws EvaluationException;
	
	/**
	 * Evaluate a Mapped<ColumnDefinition> to get turn it into a DataSetColumn
	 * @param definition
	 * @param context EvaluationContext containing parameter values, etc
	 * @return a DataSetColumn matching the parameters
	 * @throws APIException
	 */
	public EvaluatedColumnDefinition evaluateColumn(Mapped<? extends ColumnDefinition> definition, EvaluationContext context) throws APIException;
	
	/**
	 * Evaluate a ColumnDefinition to get turn it into a DataSetColumn
	 * @param definition
	 * @param context EvaluationContext containing parameter values, etc
	 * @return a DataSetColumn matching the parameters
	 * @throws APIException
	 */
	public EvaluatedColumnDefinition evaluateColumn(ColumnDefinition definition, EvaluationContext context) throws APIException;
}
