/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition;

import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.library.AllDefinitionLibraries;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.HashMap;
import java.util.Map;

/**
 * Lets you evaluate a {@link org.openmrs.module.reporting.cohort.definition.CohortDefinition} that is looked up in
 * {@link org.openmrs.module.reporting.definition.library.AllDefinitionLibraries} at evaluation time.
 *
 * This allows a report to be built against a key like "maternity patients" whose definition may change as an
 * implementation adds more encounter types, etc.
 *
 * We intentionally do not define a CachingStrategy since we are just delegating to another definition.
 */
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

    public void addParameterValue(String name, Object value) {
        if (parameterValues == null) {
            parameterValues = new HashMap<String, Object>();
        }
        parameterValues.put(name, value);
    }

    /**
     * Sets this.parameters to be the parameters from the referenced definition minus any specified in parameterValues
     * @param definitionLibraries referenced definition will be loaded from here (by definitionKey)
     */
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
