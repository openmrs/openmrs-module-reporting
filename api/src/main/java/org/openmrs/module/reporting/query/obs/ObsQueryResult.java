/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.obs;

import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.obs.definition.ObsQuery;

/**
 * Result of an Evaluated Obs Query
 */
public class ObsQueryResult extends ObsIdSet implements Evaluated<ObsQuery> {
	
	//***** PROPERTIES *****
	
    private ObsQuery definition;
    private EvaluationContext context;
    
    //***** CONSTRUCTORS *****
    
	/**
	 * Default Constructor
	 */
    public ObsQueryResult() {
    	super();
    }
    
	/**
	 * Full Constructor
	 */
    public ObsQueryResult(ObsQuery definition, EvaluationContext context) {
    	this.definition = definition;
    	this.context = context;
    } 

    //***** PROPERTY ACCESS *****

	/**
	 * @return the definition
	 */
	public ObsQuery getDefinition() {
		return definition;
	}
	
	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(ObsQuery definition) {
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
}