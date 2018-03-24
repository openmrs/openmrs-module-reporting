/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.definition.library;

import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.List;

/**
 * Simplified description of a definition from a library (safe to serialize to JSON)
 */
public class LibraryDefinitionSummary {

    private String type;

    private String key;

    private String name;

    private String description;

    // TODO determine if we need to replace this field with a DTO version
    private List<Parameter> parameters;

    public LibraryDefinitionSummary() {
    }

    public LibraryDefinitionSummary(String type, String key, String name, String description, List<Parameter> parameters) {
        this.type = type;
        this.key = key;
        this.name = name;
        this.description = description;
        this.parameters = parameters;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

}
