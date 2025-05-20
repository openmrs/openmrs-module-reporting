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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Convenience class for creating a Map of Lists
 */
public class ListMap<K, V> implements Map<K, List<V>>  {
	
	// Internal properties
	
	protected static final long serialVersionUID = 1;
	private Map<K, List<V>> underlyingMap = null;
	
	//***** Constructors *****
	
	/**
	 * Default constructor
	 */
    public ListMap() {
    	underlyingMap = new HashMap<K, List<V>>();
    }
    
    /**
     * Constructor allowing to have an underlying LinkedHashMap
     */
    public ListMap(Boolean ordered) {
    	if (ordered) {
    		underlyingMap = new LinkedHashMap<K, List<V>>();
    	}
    	else {
    		underlyingMap = new HashMap<K, List<V>>();
    	}
    }
    
    /**
     * Constructor allowing to have an underlying TreeMap
     */
    public ListMap(Comparator<K> comparator) {
    	underlyingMap = new TreeMap<K, List<V>>(comparator);
    }
    
    //***** INSTANCE METHODS *****

	/**
	 * @see Map#clear()
	 */
	public void clear() {
		underlyingMap.clear();
	}

	/**
	 * @see Map#containsKey(Object)
	 */
	public boolean containsKey(Object key) {
		return underlyingMap.containsKey(key);
	}

	/**
	 * @see Map#containsValue(Object)
	 */
	public boolean containsValue(Object value) {
		return underlyingMap.containsValue(value);
	}
	
	/**
	 * @see Map#containsValue(Object)
	 */
	public boolean containsValueInList(K key, V value) {
		List<V> l = underlyingMap.get(key);
		return l != null && l.contains(value);
	}

	/**
	 * @see Map#entrySet()
	 */
	public Set<Map.Entry<K, List<V>>> entrySet() {
		return underlyingMap.entrySet();
	}

	/**
	 * @see Map#get(Object)
	 */
	public List<V> get(Object key) {
		return underlyingMap.get(key);
	}

	/**
	 * @see Map#isEmpty()
	 */
	public boolean isEmpty() {
		return underlyingMap.isEmpty();
	}

	/**
	 * @see Map#keySet()
	 */
	public Set<K> keySet() {
		return underlyingMap.keySet();
	}

	/**
	 * @see Map#put(Object, Object)
	 */
	public List<V> put(K key, List<V> value) {
		return underlyingMap.put(key, value);
	}
	
	/**
	 * @see Map#put(Object, Object)
	 */
	public List<V> putInList(K key, V value) {
		List<V> l = get(key);
		if (l == null) {
			l = new ArrayList<V>();
			put(key, l);
		}
		l.add(value);
		return l;
	}

	/**
	 * @see Map#putAll(Map)
	 */
	public void putAll(Map<? extends K, ? extends List<V>> map) {
		underlyingMap.putAll(map);
	}
	
	/**
	 * @see Map#putAll(Map)
	 */
	public void putAll(K key, List<V> values) {
		if (values != null) {
			for (V v : values) {
				putInList(key, v);
			}
		}
	}

	/**
	 * @see Map#remove(Object)
	 */
	public List<V> remove(Object key) {
		return underlyingMap.remove(key);
	}

	/**
	 * @see Map#size()
	 */
	public int size() {
		return underlyingMap.size();
	}

	/**
	 * @see Map#values()
	 */
	public Collection<List<V>> values() {
		return underlyingMap.values();
	}
}
