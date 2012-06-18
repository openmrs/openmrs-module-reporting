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
package org.openmrs.module.reporting.encounter.query.db;

import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition;

/**
 * Encounter-related database functions
 * 
 * @see HibernateEncounterQueryDAO for default Hibernate specific dao
 */
public interface EncounterQueryDAO {
	
	/**
	 * Get encounters (not voided) restricted based on params used
	 * 
	 * @param cohort
	 * @param encounterTypes
	 * @param forms
	 * @param encounterDatetimeOnOrAfter
	 * @param encounterDatetimeOnOrBefore
	 * @param whichEncounterQualifier
	 * @return List<Encounter> encounters (not voided)
	 * @should not get voided encounters
	 */
	public List<Encounter> getEncounters(Cohort cohort, List<EncounterType> encounterTypes, List<Form> forms, Date encounterDatetimeOnOrAfter, Date encounterDatetimeOnOrBefore, TimeQualifier whichEncounterQualifier);

}
