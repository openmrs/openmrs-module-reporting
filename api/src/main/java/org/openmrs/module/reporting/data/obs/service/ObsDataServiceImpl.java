/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.obs.service;

import org.openmrs.module.reporting.data.obs.EvaluatedObsData;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * Base Implementation of ObsDataService
 */
public class ObsDataServiceImpl extends BaseDefinitionService<ObsDataDefinition> implements ObsDataService {

	/**
	 * @see DefinitionService#getDefinitionType()
	 */
    @Override
    public Class<ObsDataDefinition> getDefinitionType() {
        return ObsDataDefinition.class;
    }

	/**
	 * @see DefinitionService#evaluate(Definition, EvaluationContext)
	 */
    @Override
    public EvaluatedObsData evaluate(ObsDataDefinition definition, EvaluationContext context) throws EvaluationException {
        return (EvaluatedObsData)super.evaluate(definition, context);
    }

	/**
	 * @see DefinitionService#evaluate(Mapped, EvaluationContext)
	 */
    @Override
    public EvaluatedObsData evaluate(Mapped<? extends ObsDataDefinition> mappedDefinition, EvaluationContext context) throws EvaluationException {
        return (EvaluatedObsData)super.evaluate(mappedDefinition, context);
    }

}
