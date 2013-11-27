package org.openmrs.module.reporting.definition.library;

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

}
