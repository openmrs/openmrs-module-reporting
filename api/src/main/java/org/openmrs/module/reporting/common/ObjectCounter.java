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
