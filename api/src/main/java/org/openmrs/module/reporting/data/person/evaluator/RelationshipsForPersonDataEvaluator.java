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
package org.openmrs.module.reporting.data.person.evaluator;

import org.openmrs.Relationship;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.RelationshipsForPersonDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates an RelationshipsForPersonDataDefinition to produce a PersonData
 */
@Handler(supports=RelationshipsForPersonDataDefinition.class, order=50)
public class RelationshipsForPersonDataEvaluator implements PersonDataEvaluator {

	@Autowired
	EvaluationService evaluationService;

	/**
	 * @see org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator#evaluate(org.openmrs.module.reporting.data.person.definition.PersonDataDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @should return the obs that match the passed definition configuration
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {

		RelationshipsForPersonDataDefinition def = (RelationshipsForPersonDataDefinition) definition;
		EvaluatedPersonData pd = new EvaluatedPersonData(def, context);
		
		if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
			return pd;
		}
		if (def.getRelationshipTypes() != null && def.getRelationshipTypes().isEmpty()) {
			return pd;
		}
		if (!def.getValuesArePersonA() && !def.getValuesArePersonB()) {
			return pd;
		}

		if (def.getValuesArePersonA()) {
			addRelationshipsForPerson(pd, "B");
		}
		if (def.getValuesArePersonB()) {
			addRelationshipsForPerson(pd, "A");
		}

		return pd;
	}

	protected void addRelationshipsForPerson(EvaluatedPersonData pd, String whichPerson) {

		RelationshipsForPersonDataDefinition rpd = (RelationshipsForPersonDataDefinition)pd.getDefinition();
		String keyPerson = "person"+whichPerson.toUpperCase();

		HqlQueryBuilder qb = new HqlQueryBuilder();
		qb.select("r."+keyPerson+".personId", "r");
		qb.from(Relationship.class, "r");
		qb.whereEqual("r.voided", false);
		qb.whereIn("r.relationshipType", rpd.getRelationshipTypes());
		qb.wherePersonIn("r."+keyPerson+".personId", pd.getContext());

		List<Object[]> result = evaluationService.evaluateToList(qb, pd.getContext());
		for (Object[] row : result) {
			Integer pId = (Integer) row[0];
			Relationship r = (Relationship) row[1];
			List<Relationship> l = (List<Relationship>)pd.getData().get(pId);
			if (l == null) {
				l = new ArrayList<Relationship>();
				pd.getData().put(pId, l);
			}
			if (!l.contains(r)) {
				l.add(r);
			}
		}
	}
}
