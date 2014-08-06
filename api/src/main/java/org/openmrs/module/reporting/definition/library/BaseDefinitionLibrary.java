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

import org.apache.commons.lang.StringUtils;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.evaluation.Definition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Implementations of this class can conveniently implement on-the-fly-created reporting definitions, with inline
 * documentation on how they will behave.
 */
public abstract class BaseDefinitionLibrary<T extends Definition> implements DefinitionLibrary<T> {

    @Override
    public abstract Class<? super T> getDefinitionType();

    @Override
    public abstract String getKeyPrefix();

    public T getDefinition(String key) {
        String lookFor = key.startsWith(getKeyPrefix()) ? key.substring(getKeyPrefix().length()) : key;
        return findAndInvokeMethod(lookFor);
    }

    private T findAndInvokeMethod(String annotationValue) {
        Method method = findMethod(annotationValue);
        if (method == null) {
            return null;
        }
        return buildDefinition(method);
    }

    private T buildDefinition(Method method) {
        try {
            @SuppressWarnings("unchecked")
            T definition = (T) method.invoke(this);

			DocumentedDefinition documented = method.getAnnotation(DocumentedDefinition.class);
			String definitionCode = getDocumentedDefinitionKey(method);

            String key = getKeyPrefix() + definitionCode;
            String name = documented.name();
            if (StringUtils.isEmpty(name)) {
				name = key + ".name";
				String translatedName = MessageUtil.translate(name);
				if (translatedName.equals(name)) {
					name = ObjectUtil.fromCamelCase(definitionCode);
				}
            }
            String description = documented.definition();
            if (StringUtils.isEmpty(description)) {
                description = key + ".description";
				String translatedDescription = MessageUtil.translate(description);
				if (translatedDescription.equals(description)) {
					description = "";
				}
            }
            definition.setUuid(key);
            definition.setName(name);
            definition.setDescription(description);
            return definition;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private Method findMethod(String annotationValue) {
        for (Method candidate : this.getClass().getMethods()) {
			String definitionKey = getDocumentedDefinitionKey(candidate);
            if (definitionKey != null && definitionKey.equals(annotationValue)) {
                return candidate;
            }
        }
        return null;
    }

	private String getDocumentedDefinitionKey(Method method) {
		String definitionKey = null;
		DocumentedDefinition annotation = method.getAnnotation(DocumentedDefinition.class);
		if (annotation != null) {
			definitionKey = (annotation == null ? "" : annotation.value());
			if (ObjectUtil.isNull(definitionKey)) {
				definitionKey = method.getName();
				if (definitionKey.startsWith("get")) {
					definitionKey = definitionKey.substring(3);
				}
			}
		}
		return definitionKey;
	}

    @Override
    public List<LibraryDefinitionSummary> getDefinitionSummaries() {
        List<LibraryDefinitionSummary> summaries = new ArrayList<LibraryDefinitionSummary>();
        for (Method candidate : this.getClass().getMethods()) {
            DocumentedDefinition annotation = candidate.getAnnotation(DocumentedDefinition.class);
            if (annotation != null) {
                summaries.add(summarize(candidate));
            }
        }
        return summaries;
    }

    private LibraryDefinitionSummary summarize(Method method) {
        LibraryDefinitionSummary summary = new LibraryDefinitionSummary();
		T definition = buildDefinition(method);
        summary.setType(definition.getClass().getName());
        summary.setKey(getKeyPrefix() + getDocumentedDefinitionKey(method));
        summary.setName(definition.getName());
        summary.setDescription(definition.getDescription());
        summary.setParameters(definition.getParameters());
        return summary;
    }

    protected <T extends Parameterizable> Mapped<T> map(T parameterizable, String mappings) {
        if (parameterizable == null) {
            throw new NullPointerException("Programming error: missing parameterizable");
        }
        if (mappings == null) {
            mappings = ""; // probably not necessary, just to be safe
        }
        return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(mappings));
    }

    protected <T extends Parameterizable> Mapped<T> noMappings(T parameterizable) {
        if (parameterizable == null) {
            throw new NullPointerException("Programming error: missing parameterizable");
        }
        return new Mapped<T>(parameterizable, Collections.<String, Object>emptyMap());
    }

    protected DataConverter[] converters(Object... converterOrArray) {
        List<DataConverter> converters = new ArrayList<DataConverter>();
        for (Object o : converterOrArray) {
            if (o instanceof DataConverter) {
                converters.add((DataConverter) o);
            }
            else if (o instanceof DataConverter[]) {
                converters.addAll(Arrays.asList((DataConverter[]) o));
            }
            else if (o == null) {
                continue;
            }
            else {
                throw new IllegalArgumentException("inputs must be DataConverter or DataConverter[]");
            }
        }
        return converters.toArray(new DataConverter[converters.size()]);
    }

    public class Replacements extends HashMap<String, String> {
        public Replacements add(String key, Object replacement) {
            String asString = replacement instanceof OpenmrsObject ? ((OpenmrsObject) replacement).getId().toString() : replacement.toString();
            super.put(key, asString);
            return this;
        }
    }

}
