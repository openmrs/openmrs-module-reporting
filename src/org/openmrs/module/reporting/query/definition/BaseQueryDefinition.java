/**
 * 
 */
package org.openmrs.module.reporting.query.definition;

import org.openmrs.module.reporting.evaluation.BaseDefinition;

/**
 * Base Implementation of QueryDefinition
 */
public class BaseQueryDefinition extends BaseDefinition implements QueryDefinition {

	/**
	 * Primary key 
	 */
	private Integer id;	
	
	/**
	 * Query string property
	 */
	private String queryString;
	
	/**
	 * 
	 * @param queryString
	 */
	public BaseQueryDefinition(String queryString) { 
		this.queryString = queryString;
	}
	
	/**
	 * @see QueryDefinition#getQueryString()
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * @see OpenmrsObject#getId()
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @see OpenmrsObject#setId(Integer)
	 */
	public void setId(Integer id) {
		this.id = id;
	}
}
