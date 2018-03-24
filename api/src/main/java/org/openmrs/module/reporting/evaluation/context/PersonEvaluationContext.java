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
import java.util.List;
import java.util.Map;

import org.openmrs.OpenmrsData;
import org.openmrs.Person;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.IdSet;
import org.openmrs.module.reporting.query.person.PersonIdSet;
import org.openmrs.module.reporting.query.person.PersonQueryResult;

/**
 * Extends the patient-based EvaluationContext to add an additional Person filter for use within Person specific queries and data extraction
 * Note that this cache is cleared whenever any changes are made to basePersons
 */
public class PersonEvaluationContext extends EvaluationContext {
	
	// ***** PROPERTIES *****

	private PersonIdSet basePersons;
		
	// ***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public PersonEvaluationContext() {
		super();
	}
	
	/**
	 * Constructor which sets the Evaluation Date to a particular date
	 */
	public PersonEvaluationContext(Date evaluationDate) {
		super(evaluationDate);
	}
	
	/**
	 * Constructs a new EncounterEvaluationContext given the passed EvaluationContext and PersonQueryResult
	 */
	public PersonEvaluationContext(EvaluationContext context, PersonQueryResult basePersons) {
		super(context);
		if (context instanceof PersonEvaluationContext) {
			PersonEvaluationContext pec = (PersonEvaluationContext)context;
			this.basePersons = pec.getBasePersons();
		}
		if (this.basePersons == null) {
			this.basePersons = basePersons;
		}
		else {
			if (basePersons != null) {
				this.basePersons.getMemberIds().retainAll(basePersons.getMemberIds());
			}
		}
	}
	
	/**
	 * Constructs a new EvaluationContext given the passed EvaluationContext
	 */
	public PersonEvaluationContext(PersonEvaluationContext context) {
		super(context);
		this.basePersons = context.basePersons;
	}
	
	// *******************
	// INSTANCE METHODS 
	// *******************

	@Override
	public Map<Class<? extends OpenmrsData>, IdSet<?>> getAllBaseIdSets() {
		Map<Class<? extends OpenmrsData>, IdSet<?>> ret = super.getAllBaseIdSets();
		if (getBasePersons() != null) {
			ret.put(Person.class, getBasePersons());
		}
		return ret;
	}
	
	/**
	 * @return a shallow copy of the current instance
	 */
	@Override
	public PersonEvaluationContext shallowCopy() {
		return new PersonEvaluationContext(this);
	}

	/**
	 * @return the basePersons
	 */
	public PersonIdSet getBasePersons() {
		return basePersons;
	}

	/**
	 * @param basePersons the basePersons to set
	 */
	public void setBasePersons(PersonIdSet basePersons) {
		clearCache();
		this.basePersons = basePersons;
	}
}
