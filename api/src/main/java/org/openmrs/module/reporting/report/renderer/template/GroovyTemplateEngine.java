/**
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
package org.openmrs.module.reporting.report.renderer.template;

import groovy.lang.GroovyRuntimeException;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.util.Map;

import org.openmrs.util.OpenmrsClassLoader;

/**
 * Groovy-based template engine
 */
public class GroovyTemplateEngine implements TemplateEngine {
	
	/**
	 * @see TemplateEngine#getName()
	 */
	public String getName() {
		return "Groovy";
	}

	/**
	 * @see TemplateEngine#evaluate(String, Map)
	 */
	@Override
	public String evaluate(String template, Map<String, Object> bindings) throws TemplateEvaluationException {
		try {
			SimpleTemplateEngine engine = new SimpleTemplateEngine(OpenmrsClassLoader.getInstance());
			Template groovyTemplate = engine.createTemplate(template);
			String result = groovyTemplate.make(bindings).toString();
			return result;
		}
		catch (GroovyRuntimeException gre) {
			throw new TemplateEvaluationException(gre.getMessage(), gre);
		}
		catch (Exception e) {
			throw new TemplateEvaluationException("Unable to compile " + getName() + " template", e);
		}
	}
}
