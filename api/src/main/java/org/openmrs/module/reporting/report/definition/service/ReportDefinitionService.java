/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.definition.service;

import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.annotation.Authorized;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.api.APIException;

/**
 * ReportService API
 */
public interface ReportDefinitionService extends DefinitionService<ReportDefinition> {
	
	/**
	 * Retrieve a ReportDefinition by id primary key
	 */
	@Authorized({ ReportingConstants.PRIV_VIEW_REPORTS })
	public ReportDefinition getDefinition(Integer id);

	/**
	 * @see DefinitionService#saveDefinition(Definition)
	 */
	@Authorized({ ReportingConstants.PRIV_MANAGE_REPORT_DEFINITIONS })
	public <D extends ReportDefinition> D saveDefinition(D definition) throws APIException;

	/**
	 * This method evaluates a ReportDefinition object for the given EvaluationContext and returns a ReportData
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
	@Authorized({ ReportingConstants.PRIV_RUN_REPORTS })
	public ReportData evaluate(ReportDefinition reportDefinition, EvaluationContext context) throws EvaluationException;
	
	/**
	 * This method evaluates a ReportDefinition object for the given EvaluationContext and returns a ReportData
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
	@Authorized({ ReportingConstants.PRIV_RUN_REPORTS })
	public ReportData evaluate(Mapped<? extends ReportDefinition> reportDefinition, EvaluationContext context) throws EvaluationException;

	/**
	 * Removes the report definition given by the uuid.
	 * @param uuid the uuid of the report definition to remove
	 */
	@Authorized({ ReportingConstants.PRIV_DELETE_REPORTS })
	public void purgeDefinition(String uuid);
}

