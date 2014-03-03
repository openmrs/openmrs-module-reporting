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
package org.openmrs.module.reporting.report.definition.evaluator;

import org.openmrs.module.reporting.definition.evaluator.DefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

/**
 * Implementations of this interface can evaluate a particular subclass of {@link org.openmrs.module.reporting.report.definition.ReportDefinition} and produce
 * a {@link org.openmrs.module.reporting.report.ReportData}.
 * This is one of three interfaces that work together to define and evaluate an OpenMRS ReportData.
 * An implementation of {@link org.openmrs.module.reporting.report.definition.evaluator.ReportDefinitionEvaluator}
 * transforms one or more implementations of {@link org.openmrs.module.reporting.report.definition.ReportDefinition}
 * to produce a {@link org.openmrs.module.reporting.report.ReportData}.
 * @see org.openmrs.module.reporting.report.definition.ReportDefinition
 * @see org.openmrs.module.reporting.report.ReportData
 */
public interface ReportDefinitionEvaluator extends DefinitionEvaluator<ReportDefinition> {
	
	/**
	 * Evaluate a ReportDefinition for the given EvaluationContext
	 * 
	 * @param reportDefinition
	 * @return the evaluated ReportData
	 */
	public ReportData evaluate(ReportDefinition reportDefinition, EvaluationContext context) throws EvaluationException;
}
