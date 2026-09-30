/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort;

import java.util.HashSet;

import org.openmrs.Cohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Provides access to an evaluated {@link Cohort}, along with the 
 * {@link CohortDefinition} and {@link EvaluationContext} which
 * produced it.
 */
public class EvaluatedCohort extends PatientIdSet implements Evaluated<CohortDefinition> {

	public static final long serialVersionUID = 1L;
	
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
	 * Constructor for definition and context
	 */
	public EvaluatedCohort(CohortDefinition definition, EvaluationContext context) {
		this(null, definition, context);
	}
	
	/**
	 * Full Constructor
	 */
	public EvaluatedCohort(Cohort c, CohortDefinition definition, EvaluationContext context) {
		super(c == null ? new HashSet<Integer>() : c.getMemberIds());
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
