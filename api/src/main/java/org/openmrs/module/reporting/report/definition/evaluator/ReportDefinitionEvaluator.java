/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
