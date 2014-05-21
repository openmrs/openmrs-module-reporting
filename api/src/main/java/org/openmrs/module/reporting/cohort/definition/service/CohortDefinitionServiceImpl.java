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
package org.openmrs.module.reporting.cohort.definition.service;

import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Base Implementation of the CohortDefinitionService API
 */
@Transactional
public class CohortDefinitionServiceImpl extends BaseDefinitionService<CohortDefinition> implements CohortDefinitionService {
	
	/**
	 * @see DefinitionService#getDefinitionType()
	 */
	@Transactional(readOnly = true)
	public Class<CohortDefinition> getDefinitionType() {
		return CohortDefinition.class;
	}
	
	/**
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
	@Transactional(readOnly = true)
	@Override
	public EvaluatedCohort evaluate(CohortDefinition definition, EvaluationContext context) throws EvaluationException {
		return (EvaluatedCohort)super.evaluate(definition, context);
	}
	
	/**
	 * @see DefinitionService#evaluate(Mapped<Definition>, EvaluationContext)
	 */
	@Transactional(readOnly = true)
	@Override
	public EvaluatedCohort evaluate(Mapped<? extends CohortDefinition> definition, EvaluationContext context) throws EvaluationException {
		return (EvaluatedCohort)super.evaluate(definition, context);
	}

    /**
     * @see CohortDefinitionService#evaluateBypassingExclusionOfTestPatients(org.openmrs.module.reporting.cohort.definition.CohortDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
     */
    @Transactional(readOnly = true)
    @Override
    public EvaluatedCohort evaluateBypassingExclusionOfTestPatients(CohortDefinition definition, EvaluationContext context) throws EvaluationException {
        return (EvaluatedCohort) super.evaluateBypassingExclusionOfTestPatients(definition, context);
    }

}
