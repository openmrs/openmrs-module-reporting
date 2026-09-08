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
