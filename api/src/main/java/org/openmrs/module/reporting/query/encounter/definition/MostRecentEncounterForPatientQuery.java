/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.encounter.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.query.BaseQuery;

/**
 * Encounter Query for obtaining the single most recent encounter for each patient
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class MostRecentEncounterForPatientQuery extends BaseQuery<Encounter> implements EncounterQuery {

    public static final long serialVersionUID = 1L;
    
    //***** PROPERTIES *****
    
    @ConfigurationProperty
    private List<EncounterType> encounterTypes;
    
    @ConfigurationProperty
    private Date onOrAfter;
    
    @ConfigurationProperty
    private Date onOrBefore;
	
	//***** CONSTRUCTORS *****

	/**
	 * Default Constructor
	 */
	public MostRecentEncounterForPatientQuery() {
		super();
	}

	//***** INSTANCE METHODS *****
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Most Recent Encounter For Patient Query";
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the encounterTypes
	 */
	public List<EncounterType> getEncounterTypes() {
		return encounterTypes;
	}

	/**
	 * @param types the types to set
	 */
	public void setEncounterTypes(List<EncounterType> encounterTypes) {
		this.encounterTypes = encounterTypes;
	}
	
	/**
	 * @param encounterType the encounterType to add
	 */
	public void addEncounterType(EncounterType encounterType) {
		if (encounterTypes == null) {
			encounterTypes = new ArrayList<EncounterType>();
		}
		encounterTypes.add(encounterType);
	}

	/**
	 * @return the onOrAfter
	 */
	public Date getOnOrAfter() {
		return onOrAfter;
	}

	/**
	 * @param onOrAfter the onOrAfter to set
	 */
	public void setOnOrAfter(Date onOrAfter) {
		this.onOrAfter = onOrAfter;
	}

	/**
	 * @return the onOrBefore
	 */
	public Date getOnOrBefore() {
		return onOrBefore;
	}

	/**
	 * @param onOrBefore the onOrBefore to set
	 */
	public void setOnOrBefore(Date onOrBefore) {
		this.onOrBefore = onOrBefore;
	}
}
