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
package org.openmrs.module.reporting.report.definition.service;

import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.transaction.annotation.Transactional;

/**
 * ReportService API
 */
@Transactional
public interface ReportDefinitionService extends DefinitionService<ReportDefinition> {
	
	/**
	 * Retrieve a ReportDefinition by id primary key
	 */
	@Transactional(readOnly = true)
	public ReportDefinition getDefinition(Integer id);
	
	/**
	 * This method evaluates a ReportDefinition object for the given EvaluationContext and returns a ReportData
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
	@Transactional(readOnly = true)
	public ReportData evaluate(ReportDefinition reportDefinition, EvaluationContext context) throws EvaluationException;
	
	/**
	 * This method evaluates a ReportDefinition object for the given EvaluationContext and returns a ReportData
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
	@Transactional(readOnly = true)
	public ReportData evaluate(Mapped<? extends ReportDefinition> reportDefinition, EvaluationContext context) throws EvaluationException;
}

