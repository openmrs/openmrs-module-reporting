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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class allows one to keep track of the number of instances of a particular object that have been added to it
 */
public class ObjectCounter<T>  {

	private Map<T, Integer> underlyingMap = new HashMap<T, Integer>();

	//***** Constructors *****

    public ObjectCounter() { }


    //***** INSTANCE METHODS *****

	/**
	 * Resets the counter to zero for all objects passed into it
	 */
	public void reset() {
		underlyingMap.clear();
	}

	/**
	 * Resets the counter to zero for the object passed in
	 */
	public void reset(T o) {
		underlyingMap.remove(o);
	}

	/**
	 * @return true if the given object is in the counter
	 */
	public boolean contains(T o) {
		return underlyingMap.containsKey(o);
	}

	/**
	 * @return the current count for the given object
	 */
	public int getCount(Object o) {
		Integer i = underlyingMap.get(o);
		return (i == null ? 0 : i);
	}

	/**
	 * Increments the current count for the given object and returns the new count
	 */
	public int increment(T o) {
		int i = getCount(o);
		i++;
		underlyingMap.put(o, i);
		return i;
	}

	/**
	 * @return a map of all objects in the counter and their current counts
	 */
	public Map<T, Integer> getAllObjectCounts() {
		return Collections.unmodifiableMap(underlyingMap);
	}
}
