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

package org.openmrs.module.reporting.data.obs;

import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 *
 */
public class EvaluatedObsData extends ObsData implements Evaluated<ObsDataDefinition> {

    private ObsDataDefinition definition;
    private EvaluationContext context;

    public EvaluatedObsData() {
        super();
    }

    public EvaluatedObsData(ObsDataDefinition definition, EvaluationContext context) {
        this();
        this.definition = definition;
        this.context = context;
    }

    public ObsDataDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(ObsDataDefinition definition) {
        this.definition = definition;
    }

    public EvaluationContext getContext() {
        return context;
    }

    public void setContext(EvaluationContext context) {
        this.context = context;
    }
}
