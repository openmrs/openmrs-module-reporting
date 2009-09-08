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
package org.openmrs.module.cohort;

import org.openmrs.Cohort;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.evaluation.Evaluated;
import org.openmrs.module.evaluation.EvaluationContext;

/**
 * Provides access to an evaluated {@link Cohort}, along with the 
 * {@link CohortDefinition} and {@link EvaluationContext} which
 * produced it.
 */
public class EvaluatedCohort extends Cohort implements Evaluated<CohortDefinition> {

	private static final long serialVersionUID = 1L;
	
	//***********************
	// PROPERTIES
	//***********************
	
	private CohortDefinition definition;
	private EvaluationContext context;
	
	//***********************
	// CONSTRUCTORS
	//***********************
	
	/**
	 * Default Constructor
	 */
	public EvaluatedCohort() {
		super();
	}
	
	/**
	 * Full Constructor
	 */
	public EvaluatedCohort(Cohort c, CohortDefinition definition, EvaluationContext context) {
		super(c.getMemberIds());
		this.definition = definition;
		this.context = context;
	}
	
	//***********************
	// PROPERTY ACCESS
	//***********************

	/**
	 * @return the definition
	 */
	public CohortDefinition getDefinition() {
		return definition;
	}

	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(CohortDefinition definition) {
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
