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
package org.openmrs.module.reporting.cohort.definition.evaluator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientIdentifierCohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.query.service.DataSetQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates a PatientIdentifierCohortDefinition and produces a Cohort
 */
@Handler(supports={PatientIdentifierCohortDefinition.class})
public class PatientIdentifierCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * Default Constructor
	 */
	public PatientIdentifierCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluateCohort(CohortDefinition, EvaluationContext)
     * @should return patients who have identifiers of the passed types
     * @should return patients who have identifiers matching the passed locations
     * @should return patients who have identifiers matching the passed text
     * @should return patients who have identifiers matching the passed regular expression
     */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
    	
    	PatientIdentifierCohortDefinition picd = (PatientIdentifierCohortDefinition) cohortDefinition;

		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		
		hql.append("select 	patient.patientId");
		
		hql.append(ObjectUtil.notNull(picd.getRegexToMatch()) ? ", identifier " : " ");
		
		hql.append("from	PatientIdentifier ");
		hql.append("where	voided = false ");
		
		if (picd.getTypesToMatch() != null) {
			Set<Integer> typeIds = new HashSet<Integer>();
			for (PatientIdentifierType t : picd.getTypesToMatch()) {
				typeIds.add(t.getPatientIdentifierTypeId());
			}
			hql.append("and identifierType.patientIdentifierTypeId in (:idTypes) ");
			params.put("idTypes", typeIds);
		}
		
		if (picd.getLocationsToMatch() != null) {
			Set<Integer> locationIds = new HashSet<Integer>();
			for (Location l : picd.getLocationsToMatch()) {
				locationIds.add(l.getLocationId());
			}
			hql.append("and location.locationId in (:locationIds) ");
			params.put("locationIds", locationIds);
		}
		
		if (ObjectUtil.notNull(picd.getTextToMatch())) {
			if (picd.getTextToMatch().contains("%")) {
				hql.append("and identifier like :textToMatch ");
			}
			else {
				hql.append("and identifier = :textToMatch ");
			}
			params.put("textToMatch", picd.getTextToMatch());
		}
		
		List<Object> results = Context.getService(DataSetQueryService.class).executeHqlQuery(hql.toString(), params);
		EvaluatedCohort ret = new EvaluatedCohort(null, picd, context);
		
		if (ObjectUtil.notNull(picd.getRegexToMatch())) { // Query will return an array containing patientId and identifier
			for (Object o : results) {
				Object[] row = (Object[])o;
				if (row.length == 2 && row[1] != null && row[1].toString().matches(picd.getRegexToMatch())) {
					ret.addMember((Integer)row[0]);
				}
			}
		}
		else { // Query returns only a patientId
			for (Object o : results) {
				ret.addMember((Integer)o);
			}
		}
		
		return ret;
    }
}