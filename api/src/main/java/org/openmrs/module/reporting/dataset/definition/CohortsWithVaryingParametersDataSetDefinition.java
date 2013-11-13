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

package org.openmrs.module.reporting.dataset.definition;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluates one or more CohortDefinitions against a predefined varying set of parameters.
 * For example you might calculate an EncounterCohortDefinition, with varying values of a "locationList" parameter, to
 * determine patients seen at different locations.
 */
public class CohortsWithVaryingParametersDataSetDefinition extends BaseDataSetDefinition {

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty
    private List<Mapped<? extends CohortDefinition>> cohortDefinitions;

    @ConfigurationProperty
    private List<Map<String, Object>> varyingParameters;

    public List<Mapped<? extends CohortDefinition>> getCohortDefinitions() {
        if (cohortDefinitions == null) {
            cohortDefinitions = new ArrayList<Mapped<? extends CohortDefinition>>();
        }
        return cohortDefinitions;
    }

    public void setCohortDefinitions(List<Mapped<? extends CohortDefinition>> cohortDefinitions) {
        this.cohortDefinitions = cohortDefinitions;
    }

    public List<Map<String, Object>> getVaryingParameters() {
        if (varyingParameters == null) {
            varyingParameters = new ArrayList<Map<String, Object>>();
        }
        return varyingParameters;
    }

    public void setVaryingParameters(List<Map<String, Object>> varyingParameters) {
        this.varyingParameters = varyingParameters;
    }

    /**
     * Will automatically create "straight-through" mappings for any parameters in cd
     * @param cd
     */
    public void addCohortDefinition(CohortDefinition cd) {
        Map<String, Object> mappings = new HashMap<String, Object>();
        for (Parameter parameter : cd.getParameters()) {
            mappings.put(parameter.getName(), "${" + parameter.getName() + "}");
        }
        getCohortDefinitions().add(new Mapped<CohortDefinition>(cd, mappings));
    }

    public void addVaryingParameters(Map<String, Object> parameterOption) {
        getVaryingParameters().add(parameterOption);
    }

}
