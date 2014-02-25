package org.openmrs.module.reporting.common;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for building and executing an HQL query with parameters
 */
public class QueryBuilder {

	private StringBuilder query = new StringBuilder();
	private Map<String, Object> parameters = new HashMap<String, Object>();

    public QueryBuilder() { }

	/**
	 * Adds a clause to the query
	 */
	public QueryBuilder addClause(String clause) {
		query.append(clause).append(" ");
		return this;
	}

	public QueryBuilder addIfNotNull(String clause, String parameterName, Object parameterValue) {
		if (parameterValue != null) {
			addClause(clause).withParameter(parameterName, parameterValue);
		}
		return this;
	}

	/**
	 * Adds a parameter for the clause
	 */
	public QueryBuilder withParameter(String parameterName, Object parameterValue) {
		if (query.indexOf(":"+parameterName) == -1) {
			throw new IllegalArgumentException("You cannot add a parameter value for <" + parameterName + "> since that parameter is not defined in query: " + query);
		}
		parameters.put(parameterName, parameterValue);
		return this;
	}

	/**
	 * Executes the query and returns the result
	 */
	public List<?> execute() {
		DataSetQueryService qs = Context.getService(DataSetQueryService.class);
		return qs.executeHqlQuery(query.toString(), parameters);
	}

	/**
	 * Resets the query builder to be used to create a new query
	 */
	public void reset() {
		this.query = new StringBuilder();
		this.parameters = new HashMap<String, Object>();
	}
}
