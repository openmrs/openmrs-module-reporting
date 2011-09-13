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
package org.openmrs.module.reporting.dataset.column.evaluator;

import org.openmrs.module.reporting.dataset.column.EvaluatedColumnDefinition;
import org.openmrs.module.reporting.dataset.column.definition.ColumnDefinition;
import org.openmrs.module.reporting.definition.evaluator.DefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Each implementation of this class is expected to evaluate one or more type of ColumnDefinition.
 * Evaluating a DataSetDefinition is expected to evaluate any Columns that it is able, and add these 
 * to the passed DataSet.  A DataSetDefinition may be evaluated across many ColumnEvaluators in sequence
 * in order to produce the final DataSet.
 */
public interface ColumnEvaluator extends DefinitionEvaluator<ColumnDefinition> {
	
	/**
	 * Evaluate a DataSet for the given EvaluationContext
	 * @param dataSetDefinition
	 * @param inputCohortencounter_datetime
	 * @return the EvaluatedColumnDefinition
	 */
	public EvaluatedColumnDefinition evaluate(ColumnDefinition definition, EvaluationContext context) throws EvaluationException;
}
