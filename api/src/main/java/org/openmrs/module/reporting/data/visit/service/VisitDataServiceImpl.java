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

import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 *  Base Implementation of the VisitDataService API
 */
public class VisitDataServiceImpl extends BaseDefinitionService<VisitDataDefinition> implements VisitDataService {

	/**
	 * @see DefinitionService#getDefinitionType()
	 */
    @Override
    public Class<VisitDataDefinition> getDefinitionType() {
        return VisitDataDefinition.class;
    }

	/**
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
    @Override
    public EvaluatedVisitData evaluate(VisitDataDefinition definition, EvaluationContext context) throws EvaluationException {
        return (EvaluatedVisitData) super.evaluate(definition, context);
    }

	/**
	 * @see DefinitionService#evaluate(Mapped, EvaluationContext)
	 */
    public EvaluatedVisitData evaluate(Mapped<? extends VisitDataDefinition> mappedDefinition, EvaluationContext context) throws EvaluationException {
        return (EvaluatedVisitData) super.evaluate(mappedDefinition, context);
    }

}
