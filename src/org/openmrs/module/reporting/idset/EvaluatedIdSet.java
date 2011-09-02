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
package org.openmrs.module.reporting.idset;

import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.idset.definition.IdSetDefinition;

/**
 * Encapsulates the results of Evaluating a Filter
 */
public class EvaluatedIdSet implements Evaluated<IdSetDefinition> {
	
	//***** PROPERTIES *****
	
    private IdSetDefinition definition;
    private EvaluationContext context;
    private IdSet idSet;
    
    //***** CONSTRUCTORS *****
    
    public EvaluatedIdSet() {
    	super();
    }
    
    public EvaluatedIdSet(IdSetDefinition definition, EvaluationContext context, IdSet idSet) {
    	this.definition = definition;
    	this.context = context;
    	this.idSet = idSet;
    }
    
    //***** PROPERTY ACCESS *****

	/**
	 * @return the definition
	 */
	public IdSetDefinition getDefinition() {
		return definition;
	}
	
	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(IdSetDefinition definition) {
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
	 * @return the idSet
	 */
	public IdSet getIdSet() {
		return idSet;
	}

	/**
	 * @param idSet the idSet to set
	 */
	public void setIdSet(IdSet idSet) {
		this.idSet = idSet;
	}
}
