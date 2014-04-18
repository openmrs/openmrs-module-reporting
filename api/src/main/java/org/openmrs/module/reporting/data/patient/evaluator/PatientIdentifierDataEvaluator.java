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
package org.openmrs.module.reporting.data.patient.evaluator;

import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Evaluates a PatientIdentifierDataDefinition to produce a PatientData
 */
@Handler(supports=PatientIdentifierDataDefinition.class, order=50)
public class PatientIdentifierDataEvaluator implements PatientDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

	/** 
	 * @see PatientDataEvaluator#evaluate(PatientDataDefinition, EvaluationContext)
	 *
	 * @should return all identifiers of the specified types in order for each patient
	 * @should return all identifiers in groups according to preferred list order
	 * @should place all preferred identifiers first within type groups
	 */
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {
		
		PatientIdentifierDataDefinition def = (PatientIdentifierDataDefinition) definition;
		EvaluatedPatientData c = new EvaluatedPatientData(def, context);
		
		if ((context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) || def.getTypes() == null || def.getTypes().isEmpty()) {
			return c;
		}
	
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("pi.patient.patientId", "pi");
		q.from(PatientIdentifier.class, "pi");
		q.wherePatientIn("pi.patient.patientId", context);
		q.whereIn("pi.identifierType", def.getTypes());
		q.orderDesc("pi.preferred");

		List<Object[]> queryResult = evaluationService.evaluateToList(q);
		
		ListMap<Integer, PatientIdentifier> patIds = new ListMap<Integer, PatientIdentifier>();
		for (Object[] row : queryResult) {
			patIds.putInList((Integer) row[0], (PatientIdentifier) row[1]);
		}
		
		// Order the resulting patient identifiers by the type of identifiers passed in, followed by preferred/non-preferred
		PatientIdentifierComparator comparator = new PatientIdentifierComparator(def.getTypes());
		for (Integer pId : patIds.keySet()) {
			List<PatientIdentifier> l = patIds.get(pId);
			Collections.sort(l, comparator);
            if (def.getIncludeFirstNonNullOnly() == Boolean.TRUE) {
                c.addData(pId, l.get(0));
            }
            else {
			    c.addData(pId, l);
            }
		}
		
		return c;
	}
	
	/**
	 * Helper comparator class for sorting patient identifiers in each List
	 */
	protected class PatientIdentifierComparator implements Comparator<PatientIdentifier> {
		
		private List<PatientIdentifierType> idTypes;
		
		public PatientIdentifierComparator(List<PatientIdentifierType> idTypes) {
			this.idTypes = idTypes;
		}

		/**
		 * @see Comparator#compare(Object, Object)
		 */
		public int compare(PatientIdentifier pi1, PatientIdentifier pi2) {
			int c1 = idTypes.indexOf(pi1.getIdentifierType());
			int c2 = idTypes.indexOf(pi2.getIdentifierType());
			if (c1 == c2) {
				c1 = pi1.getPreferred() == Boolean.TRUE ? 0 : 1;
				c2 = pi2.getPreferred() == Boolean.TRUE ? 0 : 1;
			}
			return c1-c2;
		}
	}
}
