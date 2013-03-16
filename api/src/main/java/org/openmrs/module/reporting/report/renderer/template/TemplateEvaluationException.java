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

import org.openmrs.api.APIException;
import org.openmrs.module.reporting.report.renderer.RenderingException;

/**
 * Represents an Exception that occurs during evaluation of a template
 */
public class TemplateEvaluationException extends RenderingException {
	
	public static final long serialVersionUID = 1L;
	
	/**
	 * @param message
	 */
	public TemplateEvaluationException(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 */
	public TemplateEvaluationException(String message, Throwable t) {
		super(message, t);
	}
}
