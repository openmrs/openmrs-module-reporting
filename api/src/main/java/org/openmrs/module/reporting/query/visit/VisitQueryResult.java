/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.visit;

import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.visit.definition.VisitQuery;

/**
 * Result of an Evaluated Visit Query
 */
public class VisitQueryResult  extends VisitIdSet implements Evaluated<VisitQuery> {

    //***** PROPERTIES *****

    private VisitQuery definition;
    private EvaluationContext context;

    //***** CONSTRUCTORS *****

    /**
     * Default Constructor
     */
    public VisitQueryResult() {
        super();
    }

    /**
     * Full Constructor
     */
    public VisitQueryResult(VisitQuery definition, EvaluationContext context) {
        this.definition = definition;
        this.context = context;
    }

    //***** PROPERTY ACCESS *****

    /**
     * @return the definition
     */
    public VisitQuery getDefinition() {
        return definition;
    }

    /**
     * @param definition the definition to set
     */
    public void setDefinition(VisitQuery definition) {
        this.definition = definition;
    }

    /**
     * @return the context
     */
    public EvaluationContext getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(EvaluationContext context) {
        this.context = context;
    }


}
