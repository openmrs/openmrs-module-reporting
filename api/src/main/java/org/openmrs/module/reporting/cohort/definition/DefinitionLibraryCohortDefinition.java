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

package org.openmrs.module.reporting.cohort.definition;

import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.definition.library.AllDefinitionLibraries;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.Map;

@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class DefinitionLibraryCohortDefinition extends BaseCohortDefinition {

    @ConfigurationProperty
    private String definitionKey;

    @ConfigurationProperty
    private Map<String, Object> parameterValues;

    public DefinitionLibraryCohortDefinition() {
    }

    public DefinitionLibraryCohortDefinition(String definitionKey) {
        this.definitionKey = definitionKey;
    }

    public String getDefinitionKey() {
        return definitionKey;
    }

    public void setDefinitionKey(String definitionKey) {
        this.definitionKey = definitionKey;
    }

    public Map<String, Object> getParameterValues() {
        return parameterValues;
    }

    public void setParameterValues(Map<String, Object> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public void loadParameters(AllDefinitionLibraries definitionLibraries) {
        CohortDefinition realDefinition = definitionLibraries.getDefinition(CohortDefinition.class, definitionKey);

        for (Parameter parameter : realDefinition.getParameters()) {
            if (parameterValues != null && parameterValues.containsKey(parameter.getName())) {
                continue;
            }
            addParameter(parameter);
        }
    }

}
