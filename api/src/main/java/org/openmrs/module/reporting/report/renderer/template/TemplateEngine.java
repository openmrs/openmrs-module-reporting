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
