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
