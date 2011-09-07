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

import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Encapsulates the results of Evaluating a Filter
 */
public class EvaluatedQuery implements Evaluated<Query> {
	
	//***** PROPERTIES *****
	
    private Query definition;
    private EvaluationContext context;
    private QueryResult queryResult;
    
    //***** CONSTRUCTORS *****
    
    public EvaluatedQuery() {
    	super();
    }
    
    public EvaluatedQuery(Query definition, EvaluationContext context, QueryResult queryResult) {
    	this.definition = definition;
    	this.context = context;
    	this.queryResult = queryResult;
    }
    
    //***** PROPERTY ACCESS *****

	/**
	 * @return the definition
	 */
	public Query getDefinition() {
		return definition;
	}
	
	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(Query definition) {
		this.definition = definition;
	}
	
	/**
	 * @return the context
	 */
	public EvaluationContext getContext() {
		return context;
	}
	
	/**
	 * @param context the context to set
	 */
	public void setContext(EvaluationContext context) {
		this.context = context;
	}

	/**
	 * @return the queryResult
	 */
	public QueryResult getQueryResult() {
		return queryResult;
	}

	/**
	 * @param queryResult the queryResult to set
	 */
	public void setQueryResult(QueryResult queryResult) {
		this.queryResult = queryResult;
	}
}
