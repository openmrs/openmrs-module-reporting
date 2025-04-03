/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
