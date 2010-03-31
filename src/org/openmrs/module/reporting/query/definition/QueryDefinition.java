/**
 * 
 */
package org.openmrs.module.reporting.query.definition;

import org.openmrs.module.reporting.evaluation.Definition;

/**
 * 
 * The Query string should actually be a bean that encapsulates the different types of 
 * query we want to execute within the reporting framework: SQL, HQL, MDX, JDBC(?).  
 * The Query interface can be used as a 
 * 
 * The Query interface and it's derivative subclasses ...
 * 
 * TODO Need to revisit this class description once I've put together an example.
 * 
 */
public interface QueryDefinition extends Definition {
	public String getQueryString();
}
