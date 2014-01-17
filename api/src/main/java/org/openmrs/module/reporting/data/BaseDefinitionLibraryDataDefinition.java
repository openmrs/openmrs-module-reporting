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

package org.openmrs.module.reporting.data;

import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.library.AllDefinitionLibraries;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.Map;

/**
 * Base class for all types of data definitions that reference a definition from
 * {@link org.openmrs.module.reporting.definition.library.AllDefinitionLibraries}, to be looked up at evaluation time.
 *
 * The idea is to allow reports to refer to definitions with a level of indirection, so that the concrete implementation
 * may change over time.
 *
 * An example usage would look something like this:
 *
 * DefinitionLibraryPatientDataDefinition def = new DefinitionLibraryPatientDataDefinition();
 * def.setDefinitionKey("lastEncounterBeforeDateAtLocation"); // assume this has parameters onOrBefore and location
 * def.setParameterValues(buildMap("onOrBefore", DateUtil.parseYMD("2014-01-01"));
 *
 * def.loadParameters(allDefinitionLibraries);
 * // at this point def has one parameter (location) since the value of onOrBefore was provided already
 */
public abstract class BaseDefinitionLibraryDataDefinition extends BaseDataDefinition {

    @ConfigurationProperty
    private String definitionKey;

    @ConfigurationProperty
    private Map<String, Object> parameterValues;

    @Override
    public Class<?> getDataType() {
        return Object.class;
    }

    /**
     * @return the key this definition refers to (in {@link org.openmrs.module.reporting.definition.library.AllDefinitionLibraries}
     */
    public String getDefinitionKey() {
        return definitionKey;
    }

    /**
     * @param definitionKey the key this definition refers to (in {@link org.openmrs.module.reporting.definition.library.AllDefinitionLibraries}
     */
    public void setDefinitionKey(String definitionKey) {
        this.definitionKey = definitionKey;
    }

    /**
     * @return parameter values to be passed to the referenced definition at evaluation time
     */
    public Map<String, Object> getParameterValues() {
        return parameterValues;
    }

    /**
     * @param parameterValues parameter values to be passed to the referenced definition at evaluation time
     */
    public void setParameterValues(Map<String, Object> parameterValues) {
        this.parameterValues = parameterValues;
    }

    /**
     * Sets this.parameters to be the parameters from the referenced definition minus any specified in parameterValues
     * @param definitionLibraries referenced definition will be loaded from here (by definitionKey)
     */
    public void loadParameters(AllDefinitionLibraries definitionLibraries) {
        Definition realDefinition = definitionLibraries.getDefinition(null, definitionKey);

        for (Parameter parameter : realDefinition.getParameters()) {
            if (parameterValues != null && parameterValues.containsKey(parameter.getName())) {
                continue;
            }
            addParameter(parameter);
        }
    }

}
