/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.indicator.dimension;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.api.APIException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.util.IndicatorUtil;

/**
 * Represents a collection of Dimensions
 */
public class DimensionSet<T extends Dimension> implements Serializable {

	public static final long serialVersionUID = 1L;
	
	//****** PROPERTIES ******

	private Map<String, Mapped<T>> dimensions = new LinkedHashMap<String, Mapped<T>>();
	
	//****** CONSTRUCTOR ******
	
	public DimensionSet() {
		super();
	}
	
	//****** INSTANCE METHODS ******
	
	/**
	 * Returns all named dimensions in this set
	 * @return a List of all named dimensions for this Indicator
	 */
    public List<String> getDimensionKeys() {
    	return new ArrayList<String>(dimensions.keySet());
    }
    
	/**
	 * Returns true if the passed key exists
	 * @return a List of all named dimensions for this Indicator
	 */
    public boolean containsKey(String keyToCheck) {
    	for (String key : dimensions.keySet()) {
    		if (key.equalsIgnoreCase(keyToCheck)) {
    			return true;
    		}
    	}
    	return false;
    }
    
	/**
	 * Returns the dimension with the given key
	 * @return the Dimension specified with the given key
	 */
    public Mapped<T> getDimension(String dimensionKey) {
    	for (String key : dimensions.keySet()) {
    		if (key.equalsIgnoreCase(dimensionKey)) {
    			return dimensions.get(key);
    		}
    	}
    	return null;
    }

	/**
	 * Adds a Dimension with the given key
	 * @param key the key with which to associate this dimension
	 * @param dimension the dimension to add
	 * @throws APIException if the key already exists or is a reserved key
	 */
    public void addDimension(String key, Mapped<T> dimension) {
    	if (containsKey(key) || IndicatorUtil.containsIgnoreCase(Dimension.RESERVED_WORDS, key)) {
    		throw new APIException("Cannot add key <" + key + "> because it is already added or reserved.");
    	}
    	dimensions.put(key, dimension);
    } 
    
    /**
     * Returns all specified option keys for the named dimension.
     * Included in this will be a default <em>unspecified</em> option key
     * @param dimensionKey the dimension to look up
     * @return all specified options for the named dimension, including an <em>unspecified</em> option key
     */
    public List<String> getDimensionOptionKeys(String dimensionKey) {
    	Mapped<T> d = getDimension(dimensionKey);
    	if (d == null) {
    		return new ArrayList<String>();
    	}
    	return d.getParameterizable().getOptionKeys();
    }

    //****** PROPERTY ACCESS ******
	
    /**
     * @return the dimensions
     */
    public Map<String, Mapped<T>> getDimensions() {
    	return dimensions;
    }
	
    /**
     * @param dimensions the dimensions to set
     */
    public void setDimensions(Map<String, Mapped<T>> dimensions) {
    	this.dimensions = new LinkedHashMap<String, Mapped<T>>();
    	for (String key : dimensions.keySet()) {
    		addDimension(key, dimensions.get(key));
    	}
    }
}
