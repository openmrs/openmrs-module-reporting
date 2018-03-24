/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.visit.service;

import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.visit.VisitQueryResult;
import org.openmrs.module.reporting.query.visit.definition.VisitQuery;
import org.openmrs.module.reporting.evaluation.Definition;

/**
 * Interface for methods used to manage and evaluate Visit Queries
 */
public interface VisitQueryService extends DefinitionService<VisitQuery> {

    /**
     * @see DefinitionService#evaluate(Definition, EvaluationContext)
     */
    public VisitQueryResult evaluate(VisitQuery query, EvaluationContext context) throws EvaluationException;

    /**
     * @see DefinitionService#evaluate(Mapped, EvaluationContext)
     */
    public VisitQueryResult evaluate(Mapped<? extends VisitQuery> mappedQuery, EvaluationContext context) throws EvaluationException;

}
