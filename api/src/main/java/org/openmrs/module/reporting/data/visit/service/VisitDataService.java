/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.visit.service;

import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.annotation.Authorized;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.api.APIException;

/**
 * API for evaluating a VisitDataDefinition across a set of Visits
 */
public interface VisitDataService extends DefinitionService<VisitDataDefinition> {

	/**
	 * @see DefinitionService#saveDefinition(Definition)
	 */
	@Authorized({ ReportingConstants.PRIV_MANAGE_DATA_SET_DEFINITIONS })
	public <D extends VisitDataDefinition> D saveDefinition(D definition) throws APIException;
	
    @Authorized({ ReportingConstants.PRIV_RUN_REPORTS })
    public EvaluatedVisitData evaluate(VisitDataDefinition definition, EvaluationContext context) throws EvaluationException;

	/**
	 * @see DefinitionService#evaluate(Mapped, EvaluationContext)
	 */
    @Authorized({ ReportingConstants.PRIV_RUN_REPORTS })
    public EvaluatedVisitData evaluate(Mapped<? extends VisitDataDefinition> mappedDefinition, EvaluationContext context) throws EvaluationException;

}
