/*
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
