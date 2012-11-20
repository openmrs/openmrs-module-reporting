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

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.CommonsLogLogChute;

/**
 * Velocity-based template engine
 */
public class VelocityTemplateEngine implements TemplateEngine {
	
	/**
	 * @see TemplateEngine#getName()
	 */
	public String getName() {
		return "Velocity";
	}

	/**
	 * @see TemplateEngine#evaluate(String, Map)
	 */
	@Override
	public String evaluate(String template, Map<String, Object> bindings) throws TemplateEvaluationException {
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.CommonsLogLogChute");
			ve.setProperty(CommonsLogLogChute.LOGCHUTE_COMMONS_LOG_NAME, "reporttemplate_velocity");
			ve.init();
			VelocityContext velocityContext = new VelocityContext();
			for (Map.Entry<String, Object> e : bindings.entrySet()) {
				velocityContext.put(e.getKey().replace(".", "-"), e.getValue());
			}
			StringWriter writer = new StringWriter();
			ve.evaluate(velocityContext, writer, getClass().getName(), template);
			String result = writer.toString();
			return result;
		}
		catch (Exception e) {
			throw new TemplateEvaluationException("Unable to compile " + getName() + " template", e);
		}
	}
}
