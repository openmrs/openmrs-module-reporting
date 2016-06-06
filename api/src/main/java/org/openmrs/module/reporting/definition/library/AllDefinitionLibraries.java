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

package org.openmrs.module.reporting.definition.library;

import org.openmrs.module.reporting.cohort.definition.DefinitionLibraryCohortDefinition;
import org.openmrs.module.reporting.evaluation.Definition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Allows access to all definitions in all libraries
 */
@Repository
public class AllDefinitionLibraries {

    @Autowired(required = false)
    private List<DefinitionLibrary<?>> libraries;

    public List<LibraryDefinitionSummary> getDefinitionSummaries(Class<? extends Definition> returnType) {
        if (returnType == null) {
            throw new IllegalArgumentException("type is required");
        }
        List<LibraryDefinitionSummary> list = new ArrayList<LibraryDefinitionSummary>();
        if (libraries != null) {
            for (DefinitionLibrary<?> candidate : libraries) {
                if (returnType.isAssignableFrom(candidate.getDefinitionType())) {
                    list.addAll(candidate.getDefinitionSummaries());
                }
            }
        }
        return list;
    }

    public <T extends Definition> T getDefinition(Class<T> returnType, String key) {
        if (libraries != null) {
            for (DefinitionLibrary<?> candidate : libraries) {
                if (returnType == null || returnType.isAssignableFrom(candidate.getDefinitionType())) {
                    Definition definition = candidate.getDefinition(key);
                    if (definition != null) {
                        return (T) definition;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @return all the types that return values if you call getDefinitionSummaries(clazz)
     */
    public Set<Class<? extends Definition>> getAllDefinitionTypes() {
        Set<Class<? extends Definition>> types = new LinkedHashSet<Class<? extends Definition>>();
        if (libraries != null) {
            for (DefinitionLibrary<?> library : libraries) {
                types.add((Class<? extends Definition>) library.getDefinitionType());
            }
        }
        return types;
    }

    /**
     * Should only be used for testing
     * @return
     */
    List<DefinitionLibrary<?>> getLibraries() {
        return Collections.unmodifiableList(libraries);
    }

    /**
     * Constructs a CohortDefinition that references the given key, with the given specified parameter values.
     * This will cause an underlying library to build the definition (so we can determine its parameters)
     * but the returned CohortDefinition only has a reference to that definition by key
     * @param key
     * @param paramsAndValues must have an even length. Each pair is (String) paramName, (Object) paramValue
     * @return
     */
    public DefinitionLibraryCohortDefinition cohortDefinition(String key, Object... paramsAndValues) {
        DefinitionLibraryCohortDefinition cd = new DefinitionLibraryCohortDefinition(key);
        cd.loadParameters(this);
        for (int i = 0; i < paramsAndValues.length; i += 2) {
            cd.addParameterValue((String) paramsAndValues[i], paramsAndValues[i + 1]);
        }
        return cd;
    }

    /**
     * The normal way to do this should be to just define an @Component that implements DefinitionLibrary.
     * This method should only be used for testing -- we may not decide to preserve it.
     * @param library
     */
    public void addLibrary(DefinitionLibrary<?> library) {
        libraries.add(library);
    }

    /**
     * Cleans up after having manually added a library with #addLibrary
     * @param library
     */
    public void removeLibrary(DefinitionLibrary<?> library) {
        libraries.remove(library);
    }

}
