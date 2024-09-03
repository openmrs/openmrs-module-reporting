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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages all available Template Engines, provides mechanisms for discovering which
 * are available, iterating over them, and retrieving one by name
 */
public class TemplateEngineManager {
	
	private static Map<String, TemplateEngine> engines;
	static {
		engines = new LinkedHashMap<String, TemplateEngine>();
		registerTemplateEngine(new GroovyTemplateEngine());
		registerTemplateEngine(new VelocityTemplateEngine());
	}

	/**
	 * @param engine the engine you wish to register
	 */
	public static void registerTemplateEngine(TemplateEngine engine) {
		engines.put(engine.getName(), engine);
	}
	
	/**
	 * @param name the name of the engine to remove
	 */
	public static void unregisterTemplateEngine(String name) {
		engines.remove(name);
	}
	
	/**
	 * @return all available template engine names
	 */
	public static List<String> getAvailableTemplateEngineNames() {
		return new ArrayList<String>(engines.keySet());
	}
	
	/**
	 * @return the TemplateEngine registered with the passed name
	 */
	public static TemplateEngine getTemplateEngineByName(String name) {
		return engines.get(name);
	}
}
