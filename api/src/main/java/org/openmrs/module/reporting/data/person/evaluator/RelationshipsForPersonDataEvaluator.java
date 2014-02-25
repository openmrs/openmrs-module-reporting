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
import org.openmrs.module.reporting.common.QueryBuilder;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.RelationshipsForPersonDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates an RelationshipsForPersonDataDefinition to produce a PersonData
 */
@Handler(supports=RelationshipsForPersonDataDefinition.class, order=50)
public class RelationshipsForPersonDataEvaluator implements PersonDataEvaluator {

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
		if (!def.getPersonAIncluded() && !def.getPersonBIncluded()) {
			return pd;
		}

		if (def.getPersonAIncluded()) {
			addRelationshipsForPerson(pd, "A");
		}
		if (def.getPersonBIncluded()) {
			addRelationshipsForPerson(pd, "B");
		}

		return pd;
	}

	protected void addRelationshipsForPerson(EvaluatedPersonData pd, String whichPerson) {

		RelationshipsForPersonDataDefinition rpd = (RelationshipsForPersonDataDefinition)pd.getDefinition();
		String keyPerson = "person"+whichPerson.toUpperCase();

		QueryBuilder qb = new QueryBuilder();
		qb.addClause("select 	r."+keyPerson+".personId, r");
		qb.addClause("from 		Relationship as r");
		qb.addClause("where 	r.voided = false");
		qb.addIfNotNull("and	r.relationshipType in (:types) ", "types", rpd.getRelationshipTypes());
		qb.addIfNotNull("and	r." + keyPerson + ".personId in (:patientIds)", "patientIds", pd.getContext().getBaseCohort());

		for (Object o : qb.execute()) {
			Object[] parts = (Object[]) o;
			Integer pId = (Integer) parts[0];
			Relationship r = (Relationship) parts[1];
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
