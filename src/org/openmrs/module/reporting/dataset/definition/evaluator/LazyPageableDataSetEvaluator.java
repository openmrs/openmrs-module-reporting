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
package org.openmrs.module.reporting.dataset.definition.evaluator;

import java.util.Iterator;
import java.util.List;

import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.LazyPageableDataSet;
import org.openmrs.module.reporting.dataset.PageableDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PageableDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * This subinterface defines the methods needed to allow an evaluator to be wrapped in a
 * {@link LazyPageableDataSet}, making it easy to implement the evaluator for many
 * {@link PageableDataSetDefinition}s.
 */
public interface LazyPageableDataSetEvaluator extends DataSetEvaluator {

	public Iterator<DataSetRow> evaluatePartial(PageableDataSetDefinition definition, EvaluationContext context,
											List<Integer> patientIds);

	public PageableDataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws ReportingException;
	
}
