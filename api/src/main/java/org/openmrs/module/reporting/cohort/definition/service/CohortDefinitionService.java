/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition.service;

import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.annotation.Authorized;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.api.APIException;
import org.openmrs.util.PrivilegeConstants;

/**
 * Interface for methods used to manage and evaluate CohortDefinitions
 */
public interface CohortDefinitionService extends DefinitionService<CohortDefinition> {

	/**
	 * @see DefinitionService#saveDefinition(Definition)
	 */
	@Authorized({ ReportingConstants.PRIV_MANAGE_COHORT_DEFINITIONS })
	public <D extends CohortDefinition> D saveDefinition(D definition) throws APIException;
	
	@Authorized({PrivilegeConstants.GET_PATIENTS})
	public EvaluatedCohort evaluate(CohortDefinition definition, EvaluationContext context) throws EvaluationException;
	
	/**
	 * @see DefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	@Authorized({ PrivilegeConstants.GET_PATIENTS })
	public EvaluatedCohort evaluate(Mapped<? extends CohortDefinition> definition, EvaluationContext context) throws EvaluationException;

    /**
     * IF YOU ARE A NORMAL CONSUMER OF THE API, DO NOT CALL THIS METHOD! THE CORRECT CONSUMER-FACING METHOD IS
     * {@link #evaluate(org.openmrs.module.reporting.cohort.definition.CohortDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)}
     *
     * This method performs the logic of the evaluate method, but it bypasses the logic to exclude test patients. This
     * is used internally by the reporting framework in special cases, e.g. to determine <em>which</em> patients are
     * test patients.
     */
    @Authorized({ PrivilegeConstants.GET_PATIENTS })
    public EvaluatedCohort evaluateBypassingExclusionOfTestPatients(CohortDefinition definition, EvaluationContext context) throws EvaluationException;

}
