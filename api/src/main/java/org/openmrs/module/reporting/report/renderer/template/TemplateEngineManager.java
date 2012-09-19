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
