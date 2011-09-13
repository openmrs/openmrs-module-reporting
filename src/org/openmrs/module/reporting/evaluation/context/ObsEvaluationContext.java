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
package org.openmrs.module.reporting.evaluation.context;

import java.util.Date;

import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.obs.ObsIdSet;

/**
 * Extends the patient-based EvaluationContext to add an additional Obs filter for use within obs specific queries and data extraction
 * Note that this cache is cleared whenever any changes are made to baseObs
 */
public class ObsEvaluationContext extends EvaluationContext {
	
	// ***** PROPERTIES *****

	private ObsIdSet baseObs;
		
	// ***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public ObsEvaluationContext() {
		super();
	}
	
	/**
	 * Constructor which sets the Evaluation Date to a particular date
	 */
	public ObsEvaluationContext(Date evaluationDate) {
		super(evaluationDate);
	}
	
	/**
	 * Constructs a new EncounterEvaluationContext given the passed EvaluationContext and ObsIdSet
	 */
	public ObsEvaluationContext(EvaluationContext context, ObsIdSet baseObs) {
		super(context);
		this.baseObs = baseObs;
	}
	
	/**
	 * Constructs a new EvaluationContext given the passed EvaluationContext
	 */
	public ObsEvaluationContext(ObsEvaluationContext context) {
		super(context);
		this.baseObs = context.baseObs;
	}
	
	// *******************
	// INSTANCE METHODS 
	// *******************
	
	/**
	 * @return a shallow copy of the current instance
	 */
	@Override
	public ObsEvaluationContext shallowCopy() {
		return new ObsEvaluationContext(this);
	}

	/**
	 * @return the baseObs
	 */
	public ObsIdSet getBaseObs() {
		return baseObs;
	}

	/**
	 * @param baseObs the baseObs to set
	 */
	public void setBaseObs(ObsIdSet baseObs) {
		clearCache();
		this.baseObs = baseObs;
	}
}
