/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
