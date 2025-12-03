/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.visit;

import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 *
 */
public class EvaluatedVisitData extends VisitData implements Evaluated<VisitDataDefinition> {

    private VisitDataDefinition definition;
    private EvaluationContext context;

    //***** CONSTRUCTORS *****

    /**
     * Default Constructor
     */
    public EvaluatedVisitData() {
        super();
    }

    /**
     * Full Constructor
     */
    public EvaluatedVisitData(VisitDataDefinition definition, EvaluationContext context) {
        this.definition = definition;
        this.context = context;
    }


    public VisitDataDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(VisitDataDefinition definition) {
        this.definition = definition;
    }

    public EvaluationContext getContext() {
        return context;
    }

    public void setContext(EvaluationContext context) {
        this.context = context;
    }
}
