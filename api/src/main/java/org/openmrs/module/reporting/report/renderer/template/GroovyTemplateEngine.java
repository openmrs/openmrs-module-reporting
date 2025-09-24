/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
