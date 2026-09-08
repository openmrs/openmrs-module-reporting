/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.query.encounter.evaluator;

import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.encounter.EncounterQueryResult;
import org.openmrs.module.reporting.query.encounter.definition.CodedObsForEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.NumericObsForEncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.ObsForEncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = ObsForEncounterQuery.class)
public class ObsForEncounterQueryEvaluator implements EncounterQueryEvaluator {

    @Autowired
	EvaluationService evaluationService;

    @Override
    public EncounterQueryResult evaluate(EncounterQuery definition, EvaluationContext context) throws EvaluationException {

		ObsForEncounterQuery oq = (ObsForEncounterQuery) definition;
		context = ObjectUtil.nvl(context, new EvaluationContext());
		EncounterQueryResult result = new EncounterQueryResult(definition, context);

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("obs.encounter.encounterId");
		q.from(Obs.class, "obs");
		q.whereEqual("obs.concept", oq.getQuestion());
		q.whereGreaterOrEqualTo("obs.encounter.encounterDatetime", oq.getEncounterOnOrAfter());
		q.whereLessOrEqualTo("obs.encounter.encounterDatetime", oq.getEncounterOnOrBefore());
		q.whereIn("obs.encounter.encounterType", oq.getEncounterTypes());
		q.whereIn("obs.encounter.location", oq.getEncounterLocations());
		q.whereEncounterIn("obs.encounter.encounterId", context);

		if (oq instanceof NumericObsForEncounterQuery) {
			NumericObsForEncounterQuery noq = (NumericObsForEncounterQuery)oq;
			addNumericRangeCheck(q, noq.getOperator1(), noq.getValue1());
			addNumericRangeCheck(q, noq.getOperator2(), noq.getValue2());
		}

		if (oq instanceof CodedObsForEncounterQuery) {
			CodedObsForEncounterQuery coq = (CodedObsForEncounterQuery)oq;
			q.whereIn("obs.valueCoded", coq.getConceptsToInclude());
		}

		result.addAll(evaluationService.evaluateToList(q, Integer.class, context));
        return result;
    }

	protected void addNumericRangeCheck(HqlQueryBuilder q, RangeComparator operator, Double value) {
		if (operator != null || value != null) {
			String propertyName = "obs.valueNumeric";
			if (value == null) {
				q.whereNull(propertyName);
			}
			else {
				if (operator == null || operator == RangeComparator.EQUAL) {
					q.whereEqual(propertyName, value);
				}
				else if (operator == RangeComparator.GREATER_EQUAL) {
					q.whereGreaterOrEqualTo(propertyName, value);
				}
				else if (operator == RangeComparator.LESS_EQUAL) {
					q.whereLessOrEqualTo(propertyName, value);
				}
				else if (operator == RangeComparator.GREATER_THAN) {
					q.whereGreater(propertyName, value);
				}
				else if (operator == RangeComparator.LESS_THAN) {
					q.whereLess(propertyName, value);
				}
			}
		}
	}
}
