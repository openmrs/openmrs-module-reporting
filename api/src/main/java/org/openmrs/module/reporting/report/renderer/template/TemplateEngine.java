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

import java.util.Map;

/**
 * Takes a template and renders it given the passed bindings
 */
public interface TemplateEngine {
	
	/**
	 * @return the display name for this particular template engine
	 */
	public String getName();
	
	/**
	 * Takes in a String template, and a Map of bindings, and produces a generated output
	 */
	public String evaluate(String template, Map<String, Object> bindings) throws TemplateEvaluationException;
}
