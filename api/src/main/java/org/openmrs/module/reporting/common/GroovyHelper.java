/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.common;

import java.io.File;
import java.io.IOException;

import org.openmrs.util.OpenmrsClassLoader;

import groovy.lang.GroovyClassLoader;

/**
 * Contains helper methods for Groovy class loading, using a single static GroovyClassLoader
 */
public class GroovyHelper {

	private static GroovyClassLoader classLoader = new GroovyClassLoader(OpenmrsClassLoader.getInstance());

	/**
	 * Parses a .groovy file, which should define a class (not just a script).
	 * If you call this method multiple times on the same file it will keep redefining the class (so to avoid memory leaks
	 * you should typically load the class once and cache it)
	 * @param file
	 * @return
	 */
	public Class parseClassFromFile(File file) {
		try {
			Class loadedClass = classLoader.parseClass(file);
			return loadedClass;
		}
		catch (IOException ex) {
			throw new RuntimeException("Error loading: " + file, ex);
		}
	}

	/**
	 * Calls {@link #parseClassFromFile(File)} and then returns a single new instance of it.
	 * As with that underlying method, you should generally call this method once and cache the object, to avoid memory
	 * leaks.
	 * @param file
	 * @return
	 */
	public Object parseClassFromFileAndNewInstance(File file) {
		Class loadedClass = parseClassFromFile(file);
		try {
			return loadedClass.newInstance();
		}
		catch (Exception ex) {
			throw new RuntimeException("Error instantiating class: " + loadedClass, ex);
		}
	}
}
