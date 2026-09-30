/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
