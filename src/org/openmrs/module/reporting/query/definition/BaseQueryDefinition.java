/**
 * 
 */
package org.openmrs.module.reporting.query.definition;

import org.openmrs.module.reporting.evaluation.BaseDefinition;

/**
 * 
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
	 * @see org.openmrs.module.reporting.query.definition.QueryDefinition#getQueryString()
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	
}
