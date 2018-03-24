/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.evaluation.context;

import java.util.Date;
import java.util.Map;

import org.openmrs.Obs;
import org.openmrs.OpenmrsData;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.IdSet;
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

	@Override
	public Map<Class<? extends OpenmrsData>, IdSet<?>> getAllBaseIdSets() {
		Map<Class<? extends OpenmrsData>, IdSet<?>> ret = super.getAllBaseIdSets();
		if (getBaseObs() != null) {
			ret.put(Obs.class, getBaseObs());
		}
		return ret;
	}
	
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
