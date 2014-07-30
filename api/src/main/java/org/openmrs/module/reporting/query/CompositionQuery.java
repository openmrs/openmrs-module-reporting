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
import org.openmrs.module.reporting.common.BooleanOperator;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

import java.util.ArrayList;
import java.util.List;
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
	private String compositionString;

	//***** CONSTRUCTORS *****

    /**
     * Default Constructor
     */
	public CompositionQuery() {
		super();
	}

	//***** INSTANCE METHODS *****

	/**
	 * This method allows for setting the composition string and query elements based on the passed in arguments
	 */
	public void initializeFromElements(Object...elements) {
		StringBuilder s = new StringBuilder();
		int definitionCount = 0;
		for (Object o : elements) {
			String key = o.toString();
			if (o instanceof Query) {
				Q q = (Q)o;
				definitionCount++;
				key = Integer.toString(definitionCount);
				for (Parameter p : q.getParameters()) {
					if (getParameter(p.getName()) == null) {
						addParameter(p);
					}
				}
				addSearch(key, Mapped.mapStraightThrough(q));
			}
			s.append(s.length() > 0 ? " " : "").append(key);
		}
		setCompositionString(s.toString());
	}

	/**
	 * This method will initialize this CompositionQuery with the passed Queries all combined with the passed boolean operator
	 */
	public void initializeFromQueries(BooleanOperator booleanOperator, Q...elements) {
		List<Object> l = new ArrayList<Object>();
		for (Q q : elements) {
			if (!l.isEmpty()) {
				l.add(booleanOperator);
			}
			l.add(q);
		}
		initializeFromElements(l.toArray());
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
    public abstract Map<String, Mapped<Q>> getSearches();

	/**
	 * @param searches the searches to set
	 */
	public abstract void setSearches(Map<String, Mapped<Q>> searches);
    
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
}
