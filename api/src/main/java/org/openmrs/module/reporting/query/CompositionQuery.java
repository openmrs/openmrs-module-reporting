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
package org.openmrs.module.reporting.query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class that allows for the evaluation of a composition of queries of a particular type
 * This supports expressions like (QUERY1 and QUERY2) OR QUERY3
 */
public abstract class CompositionQuery<Q extends Query<T>, T extends OpenmrsObject> extends BaseQuery<T> {

	public static final long serialVersionUID = 1L;

	protected transient Log log = LogFactory.getLog(getClass());

	//***** PROPERTIES *****

	@ConfigurationProperty(required=true)
	private Map<String, Mapped<Q>> searches = new HashMap<String, Mapped<Q>>();

	@ConfigurationProperty(required=true)
	private String compositionString;

	//***** CONSTRUCTORS *****

    /**
     * Default Constructor
     */
	public CompositionQuery() {
		super();
	}
	
	//***** PROPERTY ACCESS *****
	
    /**
     * @return the compositionString
     */
    public String getCompositionString() {
    	return compositionString;
    }
	
	/**
     * @param compositionString the compositionString to set
     */
    public void setCompositionString(String compositionString) {
    	this.compositionString = compositionString;
    }
    
    /**
     * @return the searches
     */
    public Map<String, Mapped<Q>> getSearches() {
    	if (searches == null) {
    		searches = new HashMap<String, Mapped<Q>>();
    	}
    	return searches;
    }
    
    /**
     * Adds a cohort definition
     */
    public void addSearch(String key, Mapped<? extends Q> mappedDefinition) {
    	getSearches().put(key, (Mapped<Q>) mappedDefinition);
    }
    
    /**
     * Adds a cohort definition
     */
    public void addSearch(String key, Q definition, Map<String, Object> mappings) {
    	addSearch(key, new Mapped<Q>(definition, mappings));
    }

    /**
     * @param searches the searches to set
     */
    public void setSearches(Map<String, Mapped<Q>> searches) {
    	this.searches = searches;
    }
}
