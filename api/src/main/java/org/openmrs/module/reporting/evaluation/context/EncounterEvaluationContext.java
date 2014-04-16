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
import java.util.Map;

import org.openmrs.Encounter;
import org.openmrs.OpenmrsData;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.IdSet;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;

/**
 * Extends the patient-based EvaluationContext to add an additional Encounter filter for use within Encounter specific queries and data extraction
 * Note that this cache is cleared whenever any changes are made to baseEncounters
 */
public class EncounterEvaluationContext extends EvaluationContext {
	
	// ***** PROPERTIES *****

	private EncounterIdSet baseEncounters;
		
	// ***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public EncounterEvaluationContext() {
		super();
	}
	
	/**
	 * Constructor which sets the Evaluation Date to a particular date
	 */
	public EncounterEvaluationContext(Date evaluationDate) {
		super(evaluationDate);
	}
	
	/**
	 * Constructs a new EncounterEvaluationContext given the passed EvaluationContext and EncounterIdSet
	 */
	public EncounterEvaluationContext(EvaluationContext context, EncounterIdSet baseEncounters) {
		super(context);
		this.baseEncounters = baseEncounters;
	}
	
	/**
	 * Constructs a new EvaluationContext given the passed EvaluationContext
	 */
	public EncounterEvaluationContext(EncounterEvaluationContext context) {
		super(context);
		this.baseEncounters = context.baseEncounters;
	}
	
	// *******************
	// INSTANCE METHODS 
	// *******************

	@Override
	public Map<Class<? extends OpenmrsData>, IdSet<?>> getAllBaseIdSets() {
		Map<Class<? extends OpenmrsData>, IdSet<?>> ret = super.getAllBaseIdSets();
		if (getBaseEncounters() != null) {
			ret.put(Encounter.class, getBaseEncounters());
		}
		return ret;
	}
	
	/**
	 * @return a shallow copy of the current instance
	 */
	@Override
	public EncounterEvaluationContext shallowCopy() {
		return new EncounterEvaluationContext(this);
	}

	/**
	 * @return the baseEncounters
	 */
	public EncounterIdSet getBaseEncounters() {
		return baseEncounters;
	}

	/**
	 * @param baseEncounters the baseEncounters to set
	 */
	public void setBaseEncounters(EncounterIdSet baseEncounters) {
		clearCache();
		this.baseEncounters = baseEncounters;
	}
}
