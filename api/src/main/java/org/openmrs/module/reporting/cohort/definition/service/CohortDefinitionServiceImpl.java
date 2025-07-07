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
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 *  Base Implementation of the CohortDefinitionService API
 */
public class CohortDefinitionServiceImpl extends BaseDefinitionService<CohortDefinition> implements CohortDefinitionService {
	
	/**
	 * @see DefinitionService#getDefinitionType()
	 */
	public Class<CohortDefinition> getDefinitionType() {
		return CohortDefinition.class;
	}
	
	/**
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
	@Override
	public EvaluatedCohort evaluate(CohortDefinition definition, EvaluationContext context) throws EvaluationException {
		return (EvaluatedCohort)super.evaluate(definition, context);
	}
	
	/**
	 * @see DefinitionService#evaluate(Mapped, EvaluationContext)
	 */
	@Override
	public EvaluatedCohort evaluate(Mapped<? extends CohortDefinition> definition, EvaluationContext context) throws EvaluationException {
		return (EvaluatedCohort)super.evaluate(definition, context);
	}

    /**
     * @see CohortDefinitionService#evaluateBypassingExclusionOfTestPatients(CohortDefinition, EvaluationContext)
     */
    @Override
    public EvaluatedCohort evaluateBypassingExclusionOfTestPatients(CohortDefinition definition, EvaluationContext context) throws EvaluationException {
        return (EvaluatedCohort) super.evaluateBypassingExclusionOfTestPatients(definition, context);
    }

}
